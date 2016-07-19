default['java']['jdk_version'] = '8'
default['java']['arch'] = 'x86_64'
default['java']['java_home'] = '/usr/lib/jvm/java'
default['java']['openjdk_packages'] = []
default['java']['openjdk_version'] = nil
default['java']['accept_license_agreement'] = false
default['java']['set_default'] = true
default['java']['alternatives_priority'] = 1062
default['java']['set_etc_environment'] = false
default['java']['use_alt_suffix'] = true
default['java']['reset_alternatives'] = true

default['java']['ark_retries'] = 0
default['java']['ark_retry_delay'] = 2
default['java']['ark_timeout'] = 600
default['java']['ark_download_timeout'] = 600
default['java']['install_flavor'] = 'oracle'

default['java']['oracle']['accept_oracle_download_terms'] = true

default['java']['jdk']['8']['bin_cmds'] = %w(appletviewer apt ControlPanel extcheck idlj jar jarsigner java javac
                                             javadoc javafxpackager javah javap javaws jcmd jconsole jcontrol jdb
                                             jdeps jhat jinfo jjs jmap jmc jps jrunscript jsadebugd jstack
                                             jstat jstatd jvisualvm keytool native2ascii orbd pack200 policytool
                                             rmic rmid rmiregistry schemagen serialver servertool tnameserv
                                             unpack200 wsgen wsimport xjc)

default['java']['jdk']['8']['x86_64']['url'] = 'http://download.oracle.com/otn-pub/java/jdk/8u65-b17/jdk-8u65-linux-x64.tar.gz'
default['java']['jdk']['8']['x86_64']['checksum'] = '196880a42c45ec9ab2f00868d69619c0'

default['java']['oracle']['jce']['enabled'] = false
default['java']['oracle']['jce']['8']['url'] = 'http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip'
default['java']['oracle']['jce']['8']['checksum'] = 'f3020a3922efd6626c2fff45695d527f34a8020e938a49292561f18ad1320b59'
default['java']['oracle']['jce']['home'] = '/opt/java_jce'
