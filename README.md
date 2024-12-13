![Kanto logo](https://github.com/eclipse-kanto/kanto/raw/main/logo/kanto.svg)

# Eclipse Kanto - Yocto Layer

This layer provides support for building the necessary Eclipse Kanto
components.

The layer depends on the meta-virtualization one. If the edge containerization
(i.e. Eclipse Kanto container management) is to be included in your target Yocto image,
you need to enable the required by this layer virtualization.
To do that, add in the configuration file the following line:

    DISTRO_FEATURES:append = " virtualization"

For further fine-tuning and options see the meta-virtualization README.

# Dependencies

  Required for enabling the cloud connectivity and virtualization.
  Could be used as a single dependency if only suite connector
  is needed to be built.

  URI: git://github.com/openembedded/meta-openembedded.git layers:
* meta-networking

Required for enabling edge containerization

URI: git://git.yoctoproject.org/meta-virtualization

# Table of Contents

1. [Adding the required go version to your build](#adding-the-required-go-version-to-your-build)
2. [Adding the meta-kanto layer to your build](#adding-the-meta-kanto-layer-to-your-build)
3. [Required image configurations](#required-image-configurations)
4. [Example image configuration](#example-image-configuration)

## Adding the required go version to your build

The go version in poky receipes needs to be upgraded to the required one for Eclipse Kanto
since hardknott yocto version by default has go version 1.16.15.

The required go version can be found in the go.mod file of each Eclipse Kanto
repositories which needs to be included in your build.

Below described simple steps to upgrade go version

1. Clone the poky receipies: 
    git clone https://git.yoctoproject.org/poky

2. Checkout the required go version poky branch:
    git ckecout <required_go_version_poky_branch>

3. Replace the go version in path "poky\meta\recipes-devtools" with required one


## Adding the meta-kanto layer to your build

Run: 
```
bitbake-layers add-layer meta-kanto
```

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
