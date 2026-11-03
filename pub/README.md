# WxDocumentExtractor

Extract text and metadata from documents on webMethods Integration Server using [Apache Tika](https://tika.apache.org/) 3.2.3.

Tika detects the file type from the content itself, so the same services handle PDF, Microsoft Office (old and new formats), OpenDocument, HTML, XML, e-mail, RTF, plain text and many other formats. Call `wx.documentextractor:getSupportedTypes` to see exactly what is available on your server.

## Services

All services live in the `wx.documentextractor` namespace.

| Service | Purpose |
| --- | --- |
| `extractText` | Plain text and content type of a document |
| `extractWithMetadata` | Same as `extractText`, plus all Tika metadata (author, dates, page count and so on) |
| `extractRecursive` | One entry for the container and for every embedded document (zip entries, e-mail attachments, embedded Office objects) |
| `detectType` | MIME type only. The document is not parsed |
| `getSupportedTypes` | MIME types Tika can parse, the Tika version, and the jar it was loaded from |

## Input

`extractText`, `extractWithMetadata` and `extractRecursive` share the same inputs.

| Field | Type | Description |
| --- | --- | --- |
| `content` | Object | The document as `byte[]` or `java.io.InputStream` |
| `filePath` | String | Path on the Integration Server host. Used only when `content` is not supplied |
| `fileName` | String | Optional. File name used as a hint for type detection (for example `report.docx`) |
| `password` | String | Optional. Password for encrypted documents |
| `maxChars` | String | Optional. Maximum characters of text to return per document. Default `1000000`. Use `-1` for no limit |

Either `content` or `filePath` is required. If both are supplied, `content` wins.

`detectType` accepts `content`, `filePath` and `fileName` only.

## Output

### extractText

| Field | Description |
| --- | --- |
| `text` | Extracted plain text |
| `contentType` | Detected MIME type, for example `application/pdf` |
| `truncated` | `true` if the text was cut at `maxChars`, otherwise `false` |

### extractWithMetadata

Returns `text`, `contentType` and `truncated` as above, plus:

| Field | Description |
| --- | --- |
| `metadata` | Document containing every Tika metadata property. Single-valued properties are Strings, multi-valued properties are String lists |

### extractRecursive

| Field | Description |
| --- | --- |
| `documentCount` | Number of entries in `documents` |
| `documents` | Document list. Each entry has `embeddedPath`, `depth`, `contentType`, `text`, `truncated`, `metadata` and, if that item failed to parse, `error` |

The container itself is reported with `embeddedPath` of `/` and `depth` of `0`. Embedded items have a depth of 1 or more. Containers that Tika unpacks internally, such as a DOCX, also produce entries for their inner parts, so expect more entries than files.

### detectType

| Field | Description |
| --- | --- |
| `contentType` | Detected MIME type |

### getSupportedTypes

| Field | Description |
| --- | --- |
| `tikaVersion` | Version of Tika that is loaded |
| `tikaCoreLocation` | Location of the `tika-core` jar in use |
| `types` | Sorted list of supported MIME types |
| `typeCount` | Number of supported types |

## Usage notes

- Text truncation at `maxChars` is not an error. Check `truncated` if you need to know.
- Parsing failures, such as a corrupt file or a wrong password, throw a `ServiceException`. In `extractRecursive`, a failure in an embedded item is reported in that item's `error` field instead.
- An `InputStream` passed as `content` is consumed and closed by the service.
- Large files are processed with Tika's own temporary files where needed. Ensure the server's temp directory has enough space.
- OCR for images and scanned PDFs is included in Tika but only works if Tesseract is installed on the server.

## Deployment

Integration Server already ships `tika-core-3.2.3.jar` in `IntegrationServer/lib/jars`. This package deliberately uses that core and adds only the parsers, so the Tika version is fixed at 3.2.3 to match the server. Tika 4.x cannot be used, because it is not binary compatible with 3.x.

The package's `code/jars` folder holds one file, `tika-app-3.2.3.jar`, the official Apache distribution. It contains the Tika parsers and their dependencies (POI, PDFBox, Jackson, BouncyCastle and others). Its copy of `tika-core` is identical to the server's, and the server's copy is the one that is loaded.

Tika finds its parsers through `ServiceLoader`, and the server's `tika-core` can only see its own class loader, not the package's. So the services pass the package class loader to `TikaConfig` explicitly. Without that, the parsers are not found and `getSupportedTypes` returns `0` types.

After installing or reloading the package, run `getSupportedTypes` and check that `tikaVersion` is `3.2.3` and that `typeCount` is well above zero (about 240). A `typeCount` of `0` means the parsers were not found.

When upgrading Integration Server, check whether the bundled `tika-core` version has changed. If it has, replace `tika-app-3.2.3.jar` with the matching `tika-app` release from the [Tika download page](https://tika.apache.org/download.html).

Tika logs through slf4j. `tika-app-3.2.3.jar` bundles a log4j binding, so if Tika log lines or slf4j warnings appear in the server log, that is where they come from.

## Building

The Java code is in `code/source/wx/documentextractor.java`. The shared helper methods are in the class's shared source section in Designer. Compile with the Integration Server's Java runtime against `wm-isclient.jar`, `wm-isserver.jar`, the server's `tika-core-3.2.3.jar` and the jars in `code/jars`.

## Licence

Apache Tika and its dependencies are licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
