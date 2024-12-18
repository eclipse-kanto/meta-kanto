![Kanto logo](https://github.com/eclipse-kanto/kanto/raw/main/logo/kanto.svg)

# Eclipse Kanto - Essential IoT enablement for edge devices

Eclipse Kanto is a modular IoT edge software that enables devices for IoT with
all essentials like cloud connectivity, digital twins, local communication,
container management, and software updates - all configurable and remotely
manageable by an IoT cloud ecosystem of choice.

# Table of Contents
1. [Adding the meta-kanto layer to your build](#adding-the-meta-kanto-layer-to-your-build)
2. [Adding the required go version to your build](#adding-the-required-go-version-to-your-build)
3. [Required image configurations](#required-image-configurations)
4. [Adding cloud connectivity and virtualization dependencies](#adding-cloud-connectivity-and-virtualization-dependencies)
5. [Adding edge containerization to your build](#adding-edge-containerization-to-your-build)
6. [Example image configuration](#example-image-configuration)

## Adding the meta-kanto layer to your build

The meta-kanto layer is a Yocto layer designed to provide support for Eclipse Kanto,
which is part of the Eclipse IoT stack. The meta-kanto layer simplifies the process
of incorporating Eclipse Kanto components into Yocto-based images.

Run: 
```
bitbake-layers add-layer meta-kanto
```

## Adding the required go version to your build

The Go version in the poky recipes needs to be upgraded to the required version for Eclipse Kanto to build,
as the default Go version in the Hardknott Yocto release is 1.16.15.

The required Go version for Eclipse Kanto can be found in the go.mod file of each Eclipse Kanto
repository that you intend to include in your build.

Below described simple steps to upgrade go version

1. Clone the poky recipes branch which has the required Go version in separate dictonary:
    git clone https://git.yoctoproject.org/poky

2. Checkout the poky branch that includes the required Go version:
    git checkout <required_go_version_poky_branch>

3. Replace the Go version in your project build path "poky\meta\recipes-devtools" with
   checked out required Go version


## Required image configurations

The following configurations are required so that the included recipes from
the meta-kanto layer are properly installed and enabled:

1. Configure the system initialization manager to systemd
https://www.yoctoproject.org/docs/3.1/dev-manual/dev-manual.html#using-systemd-exclusively


2. Since kernel configurations are required,
you need to set the MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS variable to include kernel modules.
To do that, add in the configuration file the following line:

```
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "kernel-modules"
```

## Adding cloud connectivity and virtualization dependencies

Required for enabling the cloud connectivity and virtualization.
Could be used as a single dependency if only suite connector
is needed to be built.

URI: git://github.com/openembedded/meta-openembedded.git layers:

  * meta-networking

Required for enabling edge containerization

URI: git://git.yoctoproject.org/meta-virtualization


## Adding edge containerization to your build

The edge containerization (i.e. Eclipse Kanto container management) serves as a
specialized component within the Eclipse Kanto and if it is to be included in your
target Yocto image, you need to enable the required virtualization layer.
To do that, add in the image configuration file the following line:

    DISTRO_FEATURES:append = " virtualization"

For further fine-tuning and options see the meta-virtualization README.
https://git.yoctoproject.org/meta-virtualization/tree/README?h=hardknott

It also requires the kernel modules to be included in your target Yocto image.
To do that, add in the kernel module configuration file the following line:

    CONFIG_DM_THIN_POOL=m
    CONFIG_NF_NAT=m
    CONFIG_NF_CT_NETLINK=m
    CONFIG_NETFILTER_XT_MATCH_ADDRTYPE=m
    CONFIG_IP_NF_TARGET_MASQUERADE=m

Enabling the above mentioned kernel modules may depend on other kernel modules.
Identify the dependent modules and include them accordingly.

Once all the configurations are applied, then add the container management
software component to Yocto image.

```
# Add the Eclipse Kanto components
IMAGE_INSTALL:append = " container-management"
```


## Example image configuration

```
# Add the required DISTRO_FEATURES
DISTRO_FEATURES:append = " virtualization systemd"

# Configure the kernel modules required to be included
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "kernel-modules"

# System initialization manager setup
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"

# Add the Eclipse Kanto components
IMAGE_INSTALL:append = " suite-connector"
```

## Community

* [GitHub Issues](https://github.com/eclipse-kanto/meta-kanto/issues)
* [Mailing List](https://accounts.eclipse.org/mailing-list/kanto-dev)
