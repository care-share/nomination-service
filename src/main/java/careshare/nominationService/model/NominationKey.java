/*
 * Copyright 2016 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package careshare.nominationService.model;

import java.io.Serializable;

// Composite Primary Key Class
// See http://docs.oracle.com/javaee/5/tutorial/doc/bnbqa.html#bnbqg
// See http://www.objectdb.com/java/jpa/entity/id#Composite_Primary_Key_
public final class NominationKey implements Serializable {
    private Long id; // generated ID of this specific nomination
    public String authorId; // CareAuth User ID of the person who authored this nomination
    public String resourceId;

    public NominationKey() {}

    public NominationKey(Long id, String authorId, String resourceId) {
        this.id = id;
        this.authorId = authorId;
        this.resourceId = resourceId;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof NominationKey)) {
            return false;
        }
        NominationKey other = (NominationKey) otherOb;
        return (id == null ? other.id == null : id.equals(other.id))
                && (authorId == null ? other.authorId == null : authorId.equals(other.authorId))
                && (resourceId == null ? other.resourceId == null : resourceId.equals(other.resourceId));
    }

    public int hashCode() {
        return (authorId == null ? 0 : authorId.hashCode())
                ^ (resourceId == null ? 0 : resourceId.hashCode());
    }

    public String toString() {
        return String.format("%s/%s", authorId, resourceId);
    }
}
