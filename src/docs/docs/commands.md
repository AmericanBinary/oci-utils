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
Missing required option: '--compartment=<arg0>'
Usage: oci-utils kubectl-utils configure-localhost-context [-hV] -c=<arg0>
       [-f=<arg3>] [-k=<arg1>] [-ki=<arg2>]
creates a kubectl context corresponding to an OKE cluster
  -c, --compartment=<arg0>   compartment name
  -k, --cluster-name=<arg1>  precedence over --cluster-id, defaults to sole
                               cluster in compartment
      -ki, --cluster-id=<arg2>

  -f, --config-file=<arg3>   defaults to ${KUBECONFIG:-~/.kube/config}
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```
