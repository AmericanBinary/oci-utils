# Changelog

## `0.0.1` - `2025-08-22T00:00:00-04:00`

* add initial commands - `bu forward-kubectl/forward-mysql`, `ku`, `u co`
* publish documentation website

## `0.0.2` - `2025-10-22T10:59:20-04:00`

* add util commands - `u bastion`, `u mysql`, `u oke` (get and list)
* add new commands to docs
* finalize `shadowJar` with copy to "latest" for better debugging
* compile with parameters (to avoid picocli printing "arg0")
* add unit test for reusable "one" function (inspired by [tf's "one"][tf-one])

[tf-one]: https://developer.hashicorp.com/terraform/language/functions/one

## `0.0.3` - `2025-10-22T11:37:16-04:00`

* fix latest upload

## `0.0.4` - `2025-10-22T11:42:36-04:00`

* finish fixing latest upload

## `0.0.5` - `2025-10-22T13:21:36-04:00`

* download after checkout

## `0.0.6` - `2025-10-24T13:21:36-04:00`

* publish all installers?

## `0.0.7` - `2025-10-24T13:11:38-04:00`

* fix getting only bastion of compartment

## `0.0.8` - `2025-12-06T03:00:49-05:00`

* add `oci-utils u co g` - compartment get without name = get tenancy.
* add `oci-utils --format table`, and `--format-columns`
  when `format` is `table`. three modes: `json`, `json_pretty`, `table`.
  e.g. `oci-utils --format table u co g`
  e.g. `oci-utils --format table --format-columns id,name u co g`
* removed `NamedOciEntity`, compartment methods now use `BaseOciDataItem`
* `SessionItem` now extends `BaseOciEntity`
* `BaseOciDataItem` requires generic param T extend `BaseOciEntity`
* add `oci-utils u ci list` - for listing custom images
* the child processes are now read as they run,
  because custom images requires client side pagination
  and therefore reading large responses.
* child processes have a timeout of 15 seconds until TERM, then 5 to KILL
* add `@Dto` to model classes with centralized jackson annotations

## `0.0.9` - `2025-12-06T05:59:38-05:00`

* release new version to fix ci errors
