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
curl -fSsL $(curl -fSsL https://api.github.com/repos/AmericanBinary/oci-utils/releases/latest | jq '.assets[] | select(.name == "oci-utils") | .browser_download_url' -r) --output ~/.local/bin/oci-utils && chmod +x $_
```

### Windows/all platforms installation:

Download the `distZip` output, unzip and add `$dest/oci-utils-latest/bin` to the PATH:

Untested, since this documentation is written before the actual release:

```powershell
$release = Invoke-RestMethod -Uri "https://api.github.com/repos/AmericanBinary/oci-utils/releases/latest"
$asset = $release.assets | Where-Object { $_.name -eq "oci-utils-latest.zip" }
Invoke-WebRequest -Uri $asset.browser_download_url -OutFile "oci-utils-latest.zip"

Expand-Archive -Path "oci-utils-latest.zip" -DestinationPath "$HOME\.somewhere" -Force
$env:PATH += ";$HOME\.somewhere\oci-utils-latest\bin"
oci-utils --help
```
