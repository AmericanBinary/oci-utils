# oci-utils
helpers for working with oci command line tool (https://pypi.org/project/oci-cli/)

### single binary installation:

```shell
curl -fSsL $(
  curl -fSsL https://api.github.com/repos/AmericanBinary/oci-utils/releases/latest \
    | jq '.assets[] | select(.name == "oci-utils") | .browser_download_url' -r) --output oci-utils
chmod +x ./oci-utils
./oci-utils --version # move oci-utils into PATH
```

installation into `~/.local/bin`:

```shell
curl -fSsL $(curl -fSsL https://api.github.com/repos/AmericanBinary/oci-utils/releases/latest | jq '.assets[] | select(.name == "oci-utils") | .browser_download_url' -r) --output ~/.local/bin/oci-utils && chmod $_
```

### all platforms installation:

```shell
curl -fSsL $(
  curl -fSsL https://api.github.com/repos/AmericanBinary/oci-utils/releases/latest \
    | jq '.assets[] | select(.name == "oci-utils-latest.zip") | .browser_download_url' -r) --output oci-utils-latest.zip
unzip oci-utils-latest.zip && rm oci-utils-latest.zip
chmod +x oci-utils
./oci-utils --version # move oci-utils AND the jar file into PATH
```
