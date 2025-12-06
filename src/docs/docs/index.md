# Welcome to `oci-utils` (by American Binary)

A tool for speeding up software development on [Oracle Cloud](https://cloud.oracle.com)

## Commands

Overview of available functionality:

* `oci-utils --version`
* `oci-utils util compartments`
* `oci-utils util config`
* `oci-utils bastion-utils forward-kubectl`
* `oci-utils bastion-utils forward-mysql`
* `oci-utils kubectl configure-localhost-context`

`oci-utils` can also be used in combination with `oci`:

* `oci compute instance list -c $(oci-utils util config print | jq .DEFAULT.tenancy -r)` - list instances in root compartment
* `oci compute instance list -c $(oci-utils util compartments get --name specific | jq .id -r)` - list instances in specific compartment

`oci-utils` prints its logs to the standard error stream.
it prints it output to the standard output stream.
by default the output is in JSON format, but you can specify it:
`oci-utils --format JSON_PRETTY u compartment get` will indent,
`oci-utils --format TABLE u compartment get` will show ascii-art table.
