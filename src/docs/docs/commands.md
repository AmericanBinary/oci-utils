# Commands

## `oci-utils`

Prints top level help:

```console
$ oci-utils
Missing required subcommand
Usage: oci-utils [-hVv] [COMMAND]

  -v, --verbose   Increase verbosity. Specify multiple times to increase (-vvv).
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  bastion-utils, bu    bastion connection utilities
  kubectl-utils, ku    kubectl utilities
  util, u              general utilities
  generate-completion  Generate bash/zsh completion script for oci-utils.
```

## `oci-utils bastion-utils`

```console
$ oci-utils bastion-utils 
Missing required subcommand
Usage: oci-utils bastion-utils [-hV] [COMMAND]
bastion connection utilities
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  forward-kubectl
  forward-mysql
```

## `oci-utils bastion-utils forward-kubectl`

The compartment is specified by name, not `OCID`.

If bastion is not supplied, it assumes that there is only one in the compartment,
and that bastion is supposed to be used.

The same is true of omitting the cluster name - there should be only one in the compartment

```console
$ oci-utils bastion-utils forward-kubectl
Missing required option: '--compartment=<arg0>'
Usage: oci-utils bastion-utils forward-kubectl [-hV] [-b=<arg1>] -c=<arg0>
       [-k=<arg2>]

  -c, --compartment=<arg0>   compartment name
  -b, --bastion-name=<arg1>  defaults to sole bastion in compartment
  -k, --cluster-name=<arg2>  defaults to sole cluster in compartment
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```

## `oci-utils bastion-utils forward-kubectl`

```console
$ oci-utils bastion-utils forward-kubectl 
Missing required option: '--compartment=<arg0>'
Usage: oci-utils bastion-utils forward-kubectl [-hV] [-b=<arg1>] -c=<arg0>
       [-k=<arg2>]

  -c, --compartment=<arg0>   compartment name
  -b, --bastion-name=<arg1>  defaults to sole bastion in compartment
  -k, --cluster-name=<arg2>  defaults to sole cluster in compartment
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```

## `oci-utils kubectl-utils`

```console
$ oci-utils kubectl-utils
Missing required subcommand
Usage: oci-utils kubectl-utils [-hV] [COMMAND]
kubectl utilities
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  configure-localhost-context, clc
```

## `oci-utils ku configure-localhost-context`

This command also respects `KUBECONFIG`,
using it when `-f` is not passed,
defaulting to `~/.kube/config`.

```console
$ oci-utils ku clc
Missing required option: '--compartment=<compartment>'
Usage: oci-utils kubectl-utils configure-localhost-context [-hV]
       -c=<compartment> [-f=<file>] [-k=<clusterName>] [-ki=<clusterId>]
creates a kubectl context corresponding to an OKE cluster
  -c, --compartment=<compartment>
                             compartment name
  -k, --cluster-name=<clusterName>
                             precedence over --cluster-id, defaults to sole
                               cluster in compartment
      -ki, --cluster-id=<clusterId>

  -f, --config-file=<file>   defaults to ${KUBECONFIG:-~/.kube/config}
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```

## `oci-utils util -h`

```console
$ oci-utils u -h
Usage: oci-utils util [-hV] [COMMAND]
general utilities
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  compartments, co, comp
  config, c
  mysql                                     mysql instances
  bastion                                   bastion instances
  oke, oke-cluster, k8s, kubernetes, kubernetes-cluster
                                            kubernetes instances
  custom-images, ci                         custom compute instance images
```

## `oci-utils u ci -h`

```console
$ oci-utils u ci -h
Usage: oci-utils util custom-images [-hV] [COMMAND]
custom compute instance images
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  list, l  list custom compute instance images in tenancy
```

## `oci-utils u ci l -h`

```console
$ oci-utils u ci l -h
Usage: oci-utils util custom-images list [-hV] -c=<compartment>
list custom compute instance images in tenancy
  -c, --compartment=<compartment>
                  compartment name
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

## `oci-utils u co -h`

```console
Usage: oci-utils util compartments [-hV] [COMMAND]

  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  get, g   get compartment
  list, l  list compartments in tenancy
```

## `oci-utils u co g -h`

```console
Usage: oci-utils util compartments get [-hV] [-n=<name>]
get compartment
  -n, --name=<name>   omit for tenancy
  -h, --help          Show this help message and exit.
  -V, --version       Print version information and exit.
```

## `oci-utils u co l -h`

```console
Usage: oci-utils util compartments list [-hV]
list compartments in tenancy
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

## `oci-utils u bastion -h`

```console
$ oci-utils u bastion -h
Usage: oci-utils util bastion [-hV] [COMMAND]
bastion instances
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  get, g   get bastion instance
  list, l  list bastion instances in tenancy
```

## `oci-utils u bastion g -h`

```console
Usage: oci-utils util bastion get [-hV] -c=<compartment> [-n=<name>]
get bastion instance
  -c, --compartment=<compartment>
                      compartment name
  -n, --name=<name>
  -h, --help          Show this help message and exit.
  -V, --version       Print version information and exit.
```

## `oci-utils u bastion l -h`

```console
Usage: oci-utils util bastion list [-hV] -c=<compartment>
list bastion instances in tenancy
  -c, --compartment=<compartment>
                  compartment name
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

## `oci-utils u mysql -h`

```console
Usage: oci-utils util mysql [-hV] [COMMAND]

  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  get, g   get compartment
  list, l  list compartments in tenancy
```

## `oci-utils u oke -h`

```console
$ oci-utils u oke -h
Usage: oci-utils util oke [-hV] [COMMAND]
oke instances
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  get, g   get oke instance
  list, l  list oke instances in tenancy
```
