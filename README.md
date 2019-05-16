# Clockwork
Java Smart Work Processing 



[![Build Status](https://travis-ci.org/isocline/clockwork.svg?branch=develop)](https://travis-ci.org/isocline/clockwork)



** Clockwork 는 다양한 작업처리 방식을 하나로 통합시킨 강력한 통합 작업 처리 엔진입니다 당신이 개발한 프로세스는
실시간 처리가 필요한 경우도 있도 어떤 이벤트 처리 이후에 실행이 되어야 하는 경우도 있고 또는 스케쥴러 처럼 특정 시간에
실행되어야 하는 경우도 있습니다
이러한 각각의 상황에 맞는 라이브러리를 선택하여 매번 다른 API 를 이용하여 코딩하는 것은 비효율적인 작업입니다
또한 다른 작업 처리 방식이 서로 결합되는 경우도 존재합니다. 예를 들어 스케쥴러 기반으로 매 10분마다 작업 확인을 한후 
특정 조건을 만족하면 다른 작업 수행 시작을 알리는 이벤트를 발생시킬 수 있습니다
반대로 특정 이벤트 발생 이후에 새롭게 시작되는 스케쥴러를 시작할 수 도 있습니다

Clockwork Work Processor는 이러한 문제를 매우 손쉽게 해결해 줄 수 있습니다

## Advantages

- **Optimized Dynamic Job Proccessor**: Clockwork은 어떠한 상황에서도 작업 실행 조건을 만족시키는 다재다능한 Job 실행도구 입니다.

- **Self control process**: 작업 실행중 자신의 스케쥴 상태를 동적으로 변경이 가능하여, 다양한 Edge computing 환경과 같은 dynimic control환경에 최적화 
- **간편한 코딩**: 매우 간결하고 이해하기 쉬윈 방식으로 코딩을 할 수 있으며, 코드가 매우 심플합니다
- **다양한 확장성**: 기존 crontab 스타일의 스케쥴링 정의나 json, xml등 다양한 형태의 설정 방식을 지원합니다. 사용자에 따라 원하는 형태로 확장이 가능합니다
- **매우 정밀한 실행**: 1 ms단위로 실행을 정밀하게 조정할 수 있습니다 semi real time 수준을 지양합니다

- **아주 작은 크기**: 다른 라이브러리 종석성없이 매우 작은 크기의 라이브러리를 제공합니ㅏ



## Components

As an edge computing platform, **OpenEdge** not only provides features such as underlying service management, but also provides some basic functional modules, as follows:

- OpenEdge [Master](./doc/us-en/overview/OpenEdge-design.md#master) is responsible for the management of service instances, such as start, stop, supervise, etc., consisting of Engine, API, Command Line. And supports two modes of running service: **native** process mode and **docker** container mode
- The official module [openedge-agent](./doc/us-en/overview/OpenEdge-design.md#openedge-agent) is responsible for communication with the BIE cloud management suite, which can be used for application delivery, device information reporting, etc. Mandatory certificate authentication to ensure transmission security;
- The official module [openedge-hub](./doc/us-en/overview/OpenEdge-design.md#openedge-hub) provides message subscription and publishing functions based on the [MQTT protocol](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html), and supports four access methods: TCP, SSL, WS, and WSS;
- The official module [openedge-remote-mqtt](./doc/us-en/overview/OpenEdge-design.md#openedge-remote-mqtt) is used to bridge two MQTT Servers for message synchronization and supports configuration of multiple message route rules. ;
- The official module [openedge-function-manager](./doc/us-en/overview/OpenEdge-design.md#openedge-function-manager) provides computing power based on MQTT message mechanism, flexible, high availability, good scalability, and fast response;
- The official module [openedge-function-python27](./doc/us-en/overview/OpenEdge-design.md#openedge-function-python27) provides the Python2.7 function runtime, which can be dynamically started by `openedge-function-manager`;
- The official module [openedge-function-python36](./doc/us-en/overview/OpenEdge-design.md#openedge-function-python36) provides the Python3.6 function runtime, which can be dynamically started by `openedge-function-manager`;
- SDK (Golang) can be used to develop custom modules.

### Architecture

![Architecture](./doc/images/overview/design/openedge_design.png)

## Installation

- [Install OpenEdge on CentOS](./doc/us-en/setup/Install-OpenEdge-on-CentOS.md)
- [Install OpenEdge on Debian](./doc/us-en/setup/Install-OpenEdge-on-Debian.md)
- [Install OpenEdge on Raspbian](./doc/us-en/setup/Install-OpenEdge-on-Raspbian.md)
- [Install OpenEdge on Ubuntu](./doc/us-en/setup/Install-OpenEdge-on-Ubuntu.md)
- [Install OpenEdge on Darwin](./doc/us-en/setup/Install-OpenEdge-on-Darwin.md)
- [Build OpenEdge from Source](./doc/us-en/setup/Build-OpenEdge-from-Source.md)

## Documents

- [OpenEdge design](./doc/us-en/overview/OpenEdge-design.md)
- [OpenEdge config interpretation](./doc/us-en/tutorials/Config-interpretation.md)
- [How to write a python script for python runtime](./doc/us-en/customize/How-to-write-a-python-script-for-python-runtime.md)
- [How to import third-party libraries for Python runtime](./doc/us-en/customize/How-to-import-third-party-libraries-for-python-runtime.md)
- [How to develop a customize runtime for function](./doc/us-en/customize/How-to-develop-a-customize-runtime-for-function.md)
- [How to develop a customize module for OpenEdge](./doc/us-en/customize/How-to-develop-a-customize-module-for-OpenEdge.md)

## Contributing

If you are passionate about contributing to open source community, OpenEdge will provide you with both code contributions and document contributions. More details, please see: [How to contribute code or document to OpenEdge](./CONTRIBUTING.md).

## Discussion

As the first open edge computing framework in China, OpenEdge aims to create a lightweight, secure, reliable and scalable edge computing community that will create a good ecological environment. Here, we offer the following options for you to choose from:

- If you want to participate in OpenEdge's daily development communication, you are welcome to join [Wechat-for-OpenEdge](https://openedge.bj.bcebos.com/Wechat/Wechat-OpenEdge.png)
- If you have more about feature requirements or bug feedback of OpenEdge, please [Submit an issue](https://github.com/baidu/openedge/issues)
- If you want to know more about OpenEdge and other services of Baidu Cloud, please visit [Baidu-Cloud-forum](https://cloud.baidu.com/forum/bce)
- If you want to know more about Cloud Management Suite of BIE, please visit: [Baidu-IntelliEdge](https://cloud.baidu.com/product/bie.html)
- If you have better development advice about OpenEdge, please contact us: <contact@openedge.tech>

```java
// AppConfig application configuration
public static void main() {
    
}

type AppConfig struct {
	// specifies the version of the application configuration
	Version  string        `yaml:"version" json:"version"`
	// specifies the service information of the application
	Services []ServiceInfo `yaml:"services" json:"services" default:"[]"`
	// specifies the storage volume information of the application
	Volumes  []VolumeInfo  `yaml:"volumes" json:"volumes" default:"[]"`
}


// VolumeInfo storage volume configuration
type VolumeInfo struct {
	// specifies a unique name for the storage volume
	Name     string `yaml:"name" json:"name" validate:"regexp=^[a-zA-Z0-9][a-zA-Z0-9_-]{0\\,63}$"`
	// specifies the directory where the storage volume is on the host
	Path     string `yaml:"path" json:"path" validate:"nonzero"`
}

// MountInfo storage volume mapping configuration
type MountInfo struct {
	// specifies the name of the mapped storage volume
	Name     string `yaml:"name" json:"name" validate:"regexp=^[a-zA-Z0-9][a-zA-Z0-9_-]{0\\,63}$"`
	// specifies the directory where the storage volume is in the container
	Path     string `yaml:"path" json:"path" validate:"nonzero"`
	// specifies the operation permission of the storage volume, read-only or writable
	ReadOnly bool   `yaml:"readonly" json:"readonly"`
}

// ServiceInfo service configuration
type ServiceInfo struct {
	// specifies the unique name of the service
	Name      string            `yaml:"name" json:"name" validate:"regexp=^[a-zA-Z0-9][a-zA-Z0-9_-]{0\\,63}$"`
	// specifies the image of the service, usually using the docker image name
	Image     string            `yaml:"image" json:"image" validate:"nonzero"`
	// specifies the number of instances started
	Replica   int               `yaml:"replica" json:"replica" validate:"min=0"`
	// specifies the storage volumes that the service needs, map the storage volume to the directory in the container
	Mounts    []MountInfo       `yaml:"mounts" json:"mounts" default:"[]"`
    // specifies the port bindings which exposed by the service, only for docker container mode
	Ports     []string          `yaml:"ports" json:"ports" default:"[]"`
	// specifies the device bindings which used by the service, only for docker container mode
	Devices   []string          `yaml:"devices" json:"devices" default:"[]"`
	// specifies the startup arguments of the service program, but does not include `arg[0]`
	Args      []string          `yaml:"args" json:"args" default:"[]"`
	// specifies the environment variable of the service program
	Env       map[string]string `yaml:"env" json:"env" default:"{}"`
	// specifies the restart policy of the instance of the service
	Restart   RestartPolicyInfo `yaml:"restart" json:"restart"`
	// specifies resource limits for a single instance of the service,  only for docker container mode
	Resources Resources         `yaml:"resources" json:"resources"`
}
```
