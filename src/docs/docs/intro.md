# Getting Started

## Installation

While this tool is being released in Alpha/Preview,
the simplest way to install it is by cloning this repository and building from source.

These commands accomplish this goal:

```shell
git clone https://github.com/AmericanBinary/oci-utils.git
cd oci-utils
./gradlew assemble
./gradlew executableJarInstall # places `oci-utils` into ~/.local/bin

# to install in an alternative directory (e.g. ~/bin):
# INSTALL_DIRECTORY=~/bin ./gradlew executableJarInstall
```

On Windows (without MinGW), use `pathZipInstall` instead of `executableJarInstall`.
You may still customize the `INSTALL_DIRECTORY`.
It will install two files, a jar file and a wrapper shell script,
instead of a single file that relies on prepending `#!/usr/bin/env -S java -jar\n` to a jar file.

## Logging in

The recommended way to authenticate yourself with `oci` is to generate a config file
and place it into the `OCI_CLI_CONFIG_FILE` location, which is `~/.oci/config` by default.

> [!TIP]
>
> You can find a complete listing of `oci` environment variables here:
> [https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/clienvironmentvariables.htm][cli-env-vars].

[cli-env-vars]: https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/clienvironmentvariables.htm

The contents of the file should be as follows:

```ini
[DEFAULT]
user=${your_ocid}
fingerprint=${key_file_fingerprint}
tenancy=${tenancy_ocid}
region=${oci_region_identifier}
key_file=${HOME}/.oci/${email}_${date_of_generation}.pem
```

Here is how to find each piece of information:

**User OCID** (`user`)

* login to OCI: [https://www.oracle.com/cloud/sign-in.html](https://www.oracle.com/cloud/sign-in.html)
* Expand the profile menu by clicking the button in the top right corner, then select your profile by clicking your
  email - [https://cloud.oracle.com/identity/domains/my-profile](https://cloud.oracle.com/identity/domains/my-profile)
* Copy the `OCID` under "User information" in the middle of the page

**Adding an SSH Key** (`key_file`, `fingerprint`)

* On the "your profile" page, select the "Tokens and keys" tab
* In the "API keys" section, click the "Add API key" button, and follow the steps to generate a key pair
* download the keys to `~/.oci/`
* copy the fingerprint from the "API keys" section, after adding the API key and refreshing the list

**Tenancy OCID** (`tenancy`)

* Expand the profile menu, and select the item starting with
  `Tenancy: ` - [https://cloud.oracle.com/tenancy](https://cloud.oracle.com/tenancy)
* Copy the `OCID` under "Tenancy information"

**Pick a Region** (`region`)

* review the list of available regions here:
  [https://docs.oracle.com/en-us/iaas/Content/General/Concepts/regions.htm][oci-regions]
* it should be the region identifier, e.g. `us-ashburn-1`, `us-chicago-1`, `us-phoenix-1`

[oci-regions]: https://docs.oracle.com/en-us/iaas/Content/General/Concepts/regions.htm

## Configuring

Currently, besides configuration parameters, here are the available settings for `oci-utils`:

| setting                     | env var               | cli flag                  |
|-----------------------------|-----------------------|---------------------------|
| Profile name                | `OCI_CLI_PROFILE`     | n/a - not implemented yet |
| Configuration file location | `OCI_CLI_CONFIG_FILE` | n/a - not implemented yet |

These are inspired by the environment variables available for configuring `oci`, which are listed [here][cli-env-vars].
