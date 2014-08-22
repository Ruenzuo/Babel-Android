//
//  BABBabelManager.m
//  Babel
//
//  Created by Renzo Crisostomo on 30/07/14.
//  Copyright (c) 2014 Renzo Crisóstomo. All rights reserved.
//

#import "BABBabelManager.h"
#import "BABGitHubAPISessionHelper.h"
#import "BABTranslatorHelper.h"
#import "BABLanguage.h"
#import "BABRepository.h"
#import "BABFile.h"
#import "NSError+BABError.h"
#import "BABConfigurationHelper.h"

@interface BABBabelManager ()

@property (nonatomic, strong) BABGitHubAPISessionHelper *gitHubAPISessionHelper;
@property (nonatomic, strong) BABTranslatorHelper *translatorHelper;
@property (nonatomic, strong) BABConfigurationHelper *configurationHelper;
@property (nonatomic, strong) NSString *token;
@property (nonatomic, strong) NSMutableArray *queue;

- (BABLanguage *)randomLanguage;
- (BFTask *)randomRepositoryWithLanguage:(BABLanguage *)language;
- (BFTask *)randomFileWithLanguage:(BABLanguage *)language
                        repository:(BABRepository *)repository;
- (BFTask *)HTMLStringWithLanguage:(BABLanguage *)language
                        repository:(BABRepository *)repository
                              file:(BABFile *)file;
- (void)addNextToQueue;
- (BFTask *)nextTask;

@end

@implementation BABBabelManager

- (id)initWithToken:(NSString *)token
  andDifficultyMode:(BABDifficultyMode)difficultyMode
{
    self = [super init];
    if (self) {
        _token = token;
        _difficultyMode = difficultyMode;
        _hintLanguages = [[NSMutableArray alloc] init];
        _queue = [[NSMutableArray alloc] init];
    }
    return self;
}

#pragma mark - Properties

- (BABGitHubAPISessionHelper *)gitHubAPISessionHelper
{
    if (_gitHubAPISessionHelper == nil) {
        _gitHubAPISessionHelper = [[BABGitHubAPISessionHelper alloc] init];
    }
    return _gitHubAPISessionHelper;
}

- (BABTranslatorHelper *)translatorHelper
{
    if (_translatorHelper == nil) {
        _translatorHelper = [[BABTranslatorHelper alloc] init];
    }
    return _translatorHelper;
}

- (BABConfigurationHelper *)configurationHelper
{
    if (_configurationHelper == nil) {
        _configurationHelper = [[BABConfigurationHelper alloc] init];
    }
    return _configurationHelper;
}

- (NSArray *)languages
{
    if (_languages == nil) {
        NSError *error;
        NSData *data = [[NSFileManager defaultManager] contentsAtPath:[[NSBundle mainBundle]
                                                                       pathForResource:[self.configurationHelper fileNameForDifficultyMode:self.difficultyMode]
                                                                       ofType:@"json"]];
        NSDictionary *jsonDictionary = [NSJSONSerialization JSONObjectWithData:data
                                                                       options:0
                                                                         error:&error];
        if (error) {
            DDLogError(@"%@", [error localizedDescription]);
        }
        NSValueTransformer *valueTransformer = [MTLValueTransformer mtl_JSONArrayTransformerWithModelClass:[BABLanguage class]];
        NSArray *languages = [valueTransformer transformedValue:jsonDictionary];
        _languages = languages;
    }
    return _languages;
}

- (NSUInteger)maxHintForCurrentDifficulty
{
    return [self.configurationHelper maxHintsForDifficultyMode:self.difficultyMode];
}

#pragma mark - Private Methods

- (BABLanguage *)randomLanguage
{
    if ([self.configurationHelper shouldFixRandomLanguage]) {
        return [self.configurationHelper fixedRandomLanguage];
    } else {
        return self.languages[arc4random_uniform((int32_t)self.languages.count)];
    }
}

- (BFTask *)randomRepositoryWithLanguage:(BABLanguage *)language
{
    @weakify(self);
    
    BFTaskCompletionSource *completionSource = [BFTaskCompletionSource taskCompletionSource];
    [[self.gitHubAPISessionHelper
      repositoriesWithLanguage:language
      token:self.token]
     continueWithBlock:^id(BFTask *task) {
         
         @strongify(self);
         
         if (task.error) {
             DDLogError(@"%@", [task.error localizedDescription]);
             if (task.error.domain == AFURLResponseSerializationErrorDomain &&
                 task.error.code == -1011) {
                 [completionSource setError:[NSError bab_rateLimitReachedError]];
             } else {
                 [completionSource setError:task.error];
             }
         } else {
             NSDictionary *responseDictionary = task.result;
             NSArray *items = [responseDictionary objectForKey:@"items"];
             NSArray *repositories = [self.translatorHelper translateRepositoriesWithJSONArray:items];
             DDLogInfo(@"Repositories found: %lu", (unsigned long)[repositories count]);
             [completionSource setResult:repositories[arc4random_uniform((int32_t)5)]];
         }
         return nil;
     }];
    return completionSource.task;
}

- (BFTask *)randomFileWithLanguage:(BABLanguage *)language
                        repository:(BABRepository *)repository
{
    BFTaskCompletionSource *completionSource = [BFTaskCompletionSource taskCompletionSource];
    [[self.gitHubAPISessionHelper
      fileWithLanguage:language
      repository:repository
      token:self.token]
     continueWithBlock:^id(BFTask *task) {
         if (task.error) {
             DDLogError(@"%@", [task.error localizedDescription]);
             if (task.error.domain == AFURLResponseSerializationErrorDomain &&
                 task.error.code == -1011) {
                 [completionSource setError:[NSError bab_rateLimitReachedError]];
             } else {
                 [completionSource setError:task.error];
             }
         } else {
             NSDictionary *responseDictionary = (NSDictionary *)task.result;
             NSArray *items = [responseDictionary objectForKey:@"items"];
             NSArray *files = [self.translatorHelper translateFilesWithJSONArray:items];
             DDLogInfo(@"Files found: %lu", (unsigned long)[files count]);
             if (files.count == 0) {
                 [completionSource setError:[NSError bab_fileNotFoundError]];
             } else {
                 [completionSource setResult:files[arc4random_uniform((int32_t)files.count)]];
             }
         }
         return nil;
     }];
    return completionSource.task;
}

- (BFTask *)HTMLStringWithLanguage:(BABLanguage *)language
                        repository:(BABRepository *)repository
                              file:(BABFile *)file
{
    BFTaskCompletionSource *completionSource = [BFTaskCompletionSource taskCompletionSource];
    [[self.gitHubAPISessionHelper
      blobWithRepository:repository
      file:file
      token:self.token]
     continueWithBlock:^id(BFTask *task) {
         DDLogDebug(@"Blob done.");
         if (task.error) {
             DDLogError(@"%@", [task.error localizedDescription]);
             [completionSource setError:task.error];
         } else {
             NSDictionary *responseDictionary = (NSDictionary *)task.result;
             NSData *decodedData = [[NSData alloc] initWithBase64EncodedString:responseDictionary[@"content"]
                                                                       options:NSDataBase64DecodingIgnoreUnknownCharacters];
             NSString *decodedString = [[NSString alloc] initWithData:decodedData
                                                             encoding:NSUTF8StringEncoding];
             if (decodedString == nil) {
                 DDLogError(@"Decoding error.");
                 [completionSource setError:[NSError bab_stringDecodingError]];
             } else {
                 NSString *htmlFilePath = [[NSBundle mainBundle] pathForResource:@"index"
                                                                          ofType:@"html"
                                                                     inDirectory:@"/WebRoot"];
                 NSError *error;
                 NSString *htmlString = [NSString stringWithContentsOfFile:htmlFilePath
                                                                  encoding:NSUTF8StringEncoding
                                                                     error:&error];
                 if (error) {
                     DDLogError(@"Error decoding UTF8 string: %@", [error description]);
                     [completionSource setError:[NSError bab_stringDecodingError]];
                 } else {
                     [completionSource setResult:[[htmlString stringByReplacingOccurrencesOfString:@"BABEL_CODE_PLACEHOLDER"
                                                                                        withString:[NSString stringWithFormat:@"\n%@", decodedString]]
                                                  stringByReplacingOccurrencesOfString:@"BABEL_LANGUAGE_PLACEHOLDER"
                                                  withString:language.css]];
                 }
             }
         }
         return nil;
     }];
    return completionSource.task;
}

- (void)addNextToQueue
{
    [[self nextTask] continueWithBlock:^id(BFTask *task) {
        if (!task.error) {
            [self.queue addObject:task.result];
            DDLogInfo(@"Queue size: %lu", (unsigned long)self.queue.count);
        } else {
            DDLogError(@"Add next to queue failed with error: %@", [task.error description]);
        }
        return nil;
    }];
}

- (BFTask *)nextTask
{
    @weakify(self);
    
    BFTaskCompletionSource *completionSource = [BFTaskCompletionSource taskCompletionSource];
    BABLanguage *language = [self randomLanguage];
    __block BABRepository *repository;
    __block BABFile *file;
    [[[[self randomRepositoryWithLanguage:language]
       continueWithBlock:^id(BFTask *task) {
           
           @strongify(self);
           
           if (task.error) {
               [completionSource setError:task.error];
               return nil;
           } else {
               DDLogDebug(@"Random repository done.");
               DDLogInfo(@"Repository: %@", task.result);
               repository = task.result;
               return [self randomFileWithLanguage:language
                                        repository:repository];
           }
       }] continueWithBlock:^id(BFTask *task) {
           
           @strongify(self);
           
           if (task.error) {
               [completionSource setError:task.error];
               return nil;
           } else {
               DDLogDebug(@"Random file done.");
               DDLogInfo(@"File: %@", task.result);
               file = task.result;
               return [self HTMLStringWithLanguage:language
                                        repository:repository
                                              file:file];
           }
       }] continueWithBlock:^id(BFTask *task) {
           if (task.error) {
               [completionSource setError:task.error];
           } else {
               DDLogDebug(@"HTML string done.");
               [completionSource setResult:@{@"Language": language,
                                             @"Repository": repository,
                                             @"File": file,
                                             @"HTML": task.result}];
           }
           return nil;
       }];
    return completionSource.task;
}

#pragma mark - Public Methods

- (BFTask *)loadNext
{
    [self addNextToQueue];
    if (self.queue.count > 0) {
        NSDictionary *result = [self.queue objectAtIndex:0];
        [self.queue removeObjectAtIndex:0];
        return [BFTask taskWithResult:result];
    } else {
        return [self nextTask];
    }
}

- (void)setupQueue
{
    [self addNextToQueue];
    [self addNextToQueue];
    [self addNextToQueue];
}

@end
