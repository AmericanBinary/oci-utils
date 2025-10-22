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
