/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.prefab.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException
import kotlinx.serialization.parse
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PackageMetadataTest {
    @Test
    fun `fails if object has unknown keys`() {
        assertFailsWith(JsonDecodingException::class) {
            Json.parse<PackageMetadataV1>(
                """
                {
                    "schema_version": 1,
                    "name": "foo",
                    "dependencies": [],
                    "bar": "baz"
                }
                """.trimIndent()
            )
        }
    }

    @Test(expected = AssertionError::class)
    fun `fails if schema_version is not an integer`() {
        assertFailsWith(JsonDecodingException::class) {
            Json.parse<PackageMetadataV1>(
                """
                {
                    "schema_version": 1.0,
                    "name": "foo",
                    "dependencies": []
                }
                """.trimIndent()
            )
        }
    }

    @Test(expected = AssertionError::class)
    fun `fails if name is not a string`() {
        assertFailsWith(JsonDecodingException::class) {
            val meta = Json.parse<PackageMetadataV1>(
                """
                {
                    "schema_version": 1,
                    "name": 1,
                    "dependencies": []
                }
                """.trimIndent()
            )

            assertEquals("1", meta.name)
        }
    }

    @Test
    fun `fails if dependencies is not a list`() {
        assertFailsWith(JsonDecodingException::class) {
            Json.parse<PackageMetadataV1>(
                """
                {
                    "schema_version": 1,
                    "name": "foo",
                    "dependencies": 1
                }
                """.trimIndent()
            )
        }
    }

    // XFAIL: Can't catch that a list's contained type does not match.
    @Test(expected = AssertionError::class)
    fun `fails if dependencies is not a list of strings`() {
        assertFailsWith(JsonDecodingException::class) {
            Json.parse<PackageMetadataV1>(
                """
                {
                    "schema_version": 1,
                    "name": "foo",
                    "dependencies": ["bar", 2]
                }
                """.trimIndent()
            )
        }
    }

    @Test
    fun `valid metadata loads correctly`() {
        val packageMetadata = Json.parse<PackageMetadataV1>(
            """
            {
                "schema_version": 1,
                "name": "foo",
                "dependencies": [
                    "bar",
                    "baz"
                ]
            }
            """.trimIndent()
        )

        assertEquals(1, packageMetadata.schemaVersion)
        assertEquals("foo", packageMetadata.name)
        assertEquals(listOf("bar", "baz"), packageMetadata.dependencies)
    }
}
