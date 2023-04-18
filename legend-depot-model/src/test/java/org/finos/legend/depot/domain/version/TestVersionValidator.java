//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.domain.version;

import org.junit.Assert;
import org.junit.Test;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestVersionValidator
{
    @Test
    public void testVersionsAreCorrect()
    {
        Assert.assertTrue(VersionValidator.isValid("1.1.1"));
        Assert.assertTrue(VersionValidator.isValid(MASTER_SNAPSHOT));
        Assert.assertFalse(VersionValidator.isValid("jkwhfkjasf-jhdfjks"));
        Assert.assertTrue(VersionValidator.isValid("my-SNAPSHOT"));
    }

}
