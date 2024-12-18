# hello-vertx-1

Set up a MySQL DB.

```
$ docker run \
  --name some-mysql \
  -e MYSQL_ROOT_PASSWORD=my-secret-pw \
  -e MYSQL_DATABASE=my_db \
  -p 3307:3306 \
  -d mysql:latest
```

Connect to the MySQL DB.

```
$ mysql -h 127.0.0.1 \
  -P 3307 \
  -u root \
  -pmy-secret-pw
```

Create the `test` table.

```
mysql> use my_db;
mysql> create table test(id int primary key, name varchar(255));
```

Build a jar.

```
$ ./gradlew build
```

Run without dd-java-agent.jar

```
$ java -jar ./build/libs/starter-1.0.0-SNAPSHOT-fat.jar
Dec 18, 2024 4:12:31 PM io.netty.resolver.dns.DnsServerAddressStreamProviders <clinit>
WARNING: Can not find io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'
HTTP server started on port 8888
Dec 18, 2024 4:12:31 PM io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer
INFO: Succeeded in deploying verticle
db method duration: 30 milliseconds
insert duration: 454 milliseconds
rows = 100000
```

Run with dd-java-agent.jar

```
$ java -javaagent:./dd-java-agent.jar \
  -Ddd.trace.sample.rate=1 \
  -Ddd.service=hb-starter \
  -Ddd.env=dev \
  -Ddd.version=0.0.1 \
  -jar build/libs/starter-1.0.0-SNAPSHOT-fat.jar
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
Dec 18, 2024 4:16:09 PM io.netty.resolver.dns.DnsServerAddressStreamProviders <clinit>
WARNING: Can not find io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'
HTTP server started on port 8888
Dec 18, 2024 4:16:09 PM io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer
INFO: Succeeded in deploying verticle
[dd.trace 2024-12-18 16:16:09:932 +0800] [dd-task-scheduler] INFO datadog.trace.agent.core.StatusLogger - DATADOG TRACER CONFIGURATION {"version":"1.44.1~13a9a2d011","os_name":"Mac OS X","os_version":"14.7.1","architecture":"aarch64","lang":"jvm","lang_version":"21.0.5","jvm_vendor":"Amazon.com Inc.","jvm_version":"21.0.5+11-LTS","java_class_version":"65.0","http_nonProxyHosts":"null","http_proxyHost":"null","enabled":true,"service":"hb-starter","agent_url":"http://localhost:8126","agent_error":false,"debug":false,"trace_propagation_style_extract":["datadog","tracecontext"],"trace_propagation_style_inject":["datadog","tracecontext"],"analytics_enabled":false,"sample_rate":1.0,"priority_sampling_enabled":true,"logs_correlation_enabled":true,"profiling_enabled":false,"remote_config_enabled":true,"debugger_enabled":false,"debugger_exception_enabled":false,"debugger_span_origin_enabled":false,"appsec_enabled":"ENABLED_INACTIVE","rasp_enabled":true,"telemetry_enabled":true,"telemetry_dependency_collection_enabled":true,"telemetry_log_collection_enabled":true,"dd_version":"0.0.1","health_checks_enabled":true,"configuration_file":"no config file present","runtime_id":"a9c5edb3-bc85-48e8-b260-1e500effeb01","logging_settings":{"levelInBrackets":false,"dateTimeFormat":"'[dd.trace 'yyyy-MM-dd HH:mm:ss:SSS Z']'","logFile":"System.err","configurationFile":"simplelogger.properties","showShortLogName":false,"showDateTime":true,"showLogName":true,"showThreadName":true,"defaultLogLevel":"INFO","warnLevelString":"WARN","embedException":false},"cws_enabled":false,"cws_tls_refresh":5000,"datadog_profiler_enabled":false,"datadog_profiler_safe":true,"datadog_profiler_enabled_overridden":false,"data_streams_enabled":false}
db method duration: 1793 milliseconds
insert duration: 2182 milliseconds
rows = 100000
```

* `db method duration` jumps from 30 milliseconds to 1793 milliseconds
* `insert duration` jumps from 454 milliseconds to 2182 milliseconds

Lowering the sample rate to 0.1 does not help.

```
➜ java -javaagent:./dd-java-agent.jar \
  -Ddd.trace.sample.rate=0.1 \
  -Ddd.service=hb-starter \
  -Ddd.env=dev \
  -Ddd.version=0.0.1 \
  -jar build/libs/starter-1.0.0-SNAPSHOT-fat.jar
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
Dec 18, 2024 4:36:36 PM io.netty.resolver.dns.DnsServerAddressStreamProviders <clinit>
WARNING: Can not find io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'
HTTP server started on port 8888
Dec 18, 2024 4:36:36 PM io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer
INFO: Succeeded in deploying verticle
[dd.trace 2024-12-18 16:36:36:928 +0800] [dd-task-scheduler] INFO datadog.trace.agent.core.StatusLogger - DATADOG TRACER CONFIGURATION {"version":"1.44.1~13a9a2d011","os_name":"Mac OS X","os_version":"14.7.1","architecture":"aarch64","lang":"jvm","lang_version":"21.0.5","jvm_vendor":"Amazon.com Inc.","jvm_version":"21.0.5+11-LTS","java_class_version":"65.0","http_nonProxyHosts":"null","http_proxyHost":"null","enabled":true,"service":"hb-starter","agent_url":"http://localhost:8126","agent_error":false,"debug":false,"trace_propagation_style_extract":["datadog","tracecontext"],"trace_propagation_style_inject":["datadog","tracecontext"],"analytics_enabled":false,"sample_rate":0.1,"priority_sampling_enabled":true,"logs_correlation_enabled":true,"profiling_enabled":false,"remote_config_enabled":true,"debugger_enabled":false,"debugger_exception_enabled":false,"debugger_span_origin_enabled":false,"appsec_enabled":"ENABLED_INACTIVE","rasp_enabled":true,"telemetry_enabled":true,"telemetry_dependency_collection_enabled":true,"telemetry_log_collection_enabled":true,"dd_version":"0.0.1","health_checks_enabled":true,"configuration_file":"no config file present","runtime_id":"6678a709-5f1f-445e-adaa-51a2c164e042","logging_settings":{"levelInBrackets":false,"dateTimeFormat":"'[dd.trace 'yyyy-MM-dd HH:mm:ss:SSS Z']'","logFile":"System.err","configurationFile":"simplelogger.properties","showShortLogName":false,"showDateTime":true,"showLogName":true,"showThreadName":true,"defaultLogLevel":"INFO","warnLevelString":"WARN","embedException":false},"cws_enabled":false,"cws_tls_refresh":5000,"datadog_profiler_enabled":false,"datadog_profiler_safe":true,"datadog_profiler_enabled_overridden":false,"data_streams_enabled":false}
db method duration: 1786 milliseconds
insert duration: 2194 milliseconds
rows = 100000
```
