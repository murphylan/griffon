/*
* Copyright 2011 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.codehaus.griffon.artifacts.ArtifactInstallEngine

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_resolve_dependencies_called')) return
_resolve_dependencies_called = true

includeTargets << griffonScript('_GriffonArtifacts')

target('resolveDependencies': '') {
    ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine()
    if (!artifactInstallEngine.resolvePluginDependencies()) {
        exit(1)
    }

    pluginSettings.resolveAndAddAllPluginDependencies()
}
