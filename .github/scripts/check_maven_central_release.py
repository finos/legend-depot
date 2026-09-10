#!/usr/bin/env python3

# Copyright 2026 Goldman Sachs
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import json
import sys
import urllib.parse
import urllib.request
from urllib.error import HTTPError, URLError


def main() -> int:
    if len(sys.argv) != 2:
        print("::error::Expected exactly one release version argument.")
        return 1

    release_version = sys.argv[1]
    query = urllib.parse.urlencode(
        {
            "q": f'g:"org.finos.legend.depot" AND a:"legend-depot" AND v:"{release_version}"',
            "rows": 1,
            "wt": "json",
        }
    )
    url = f"https://search.maven.org/solrsearch/select?{query}"
    try:
        with urllib.request.urlopen(url, timeout=30) as response:
            if response.status != 200:
                print(
                    "::error::Failed to verify Maven Central availability for "
                    f"version {release_version}: unexpected HTTP status {response.status}"
                )
                return 1
            match_count = json.load(response)["response"]["numFound"]
    except (HTTPError, URLError, TimeoutError, json.JSONDecodeError, KeyError) as error:
        print(f"::error::Failed to verify Maven Central availability for version {release_version}: {error}")
        return 1

    if match_count != 0:
        print(f"::error::Artifacts for version {release_version} already exist in Maven Central.")
        return 1

    print(f"Version {release_version} is available for release.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
