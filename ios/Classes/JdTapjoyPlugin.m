#import "JdTapjoyPlugin.h"
#if __has_include(<jd_tapjoy_plugin/jd_tapjoy_plugin-Swift.h>)
#import <jd_tapjoy_plugin/jd_tapjoy_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "jd_tapjoy_plugin-Swift.h"
#endif

@implementation JdTapjoyPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftJdTapjoyPlugin registerWithRegistrar:registrar];
}
@end
