/*
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.utils;

import azkaban.executor.ExecutableFlowBase;
import azkaban.executor.ExecutableNode;
import azkaban.executor.Status;
import java.util.List;
import java.util.Map;


public class FlowUtils {

  /**
   * Change job status to disabled in exflow if the job is in disabledJobs
   */
  public static void applyDisabledJobs(final List<Object> disabledJobs,
      final ExecutableFlowBase exflow) {
    for (final Object disabled : disabledJobs) {
      if (disabled instanceof String) {
        final String nodeName = (String) disabled;
        final ExecutableNode node = exflow.getExecutableNode(nodeName);
        if (node != null) {
          node.setStatus(Status.DISABLED);
        }
      } else if (disabled instanceof Map) {
        final Map<String, Object> nestedDisabled = (Map<String, Object>) disabled;
        final String nodeName = (String) nestedDisabled.get("id");
        final List<Object> subDisabledJobs =
            (List<Object>) nestedDisabled.get("children");

        if (nodeName == null || subDisabledJobs == null) {
          return;
        }

        final ExecutableNode node = exflow.getExecutableNode(nodeName);
        if (node != null && node instanceof ExecutableFlowBase) {
          applyDisabledJobs(subDisabledJobs, (ExecutableFlowBase) node);
        }
      }
    }
  }
}
