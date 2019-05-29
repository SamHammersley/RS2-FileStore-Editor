# RuneScape 2 FileStore Editor
Software to view/edit the cache or local file store of older revisions of RuneScape 2.

## Format
The Runescape file store structure consists of a data file, containing many 520 byte data chunks for each file in the file store, and index files, containing the size and initial data chunk id (within the data file) for each file.

### Data Chunks
The data file, previously mentioned, is broken down into 520 byte data chunks; an 8 byte header and 512 bytes of raw data for a file.
The structure of the header part is as follows: 

Data | Description
------------ | -------------
Short (16-bit) | The id of the file this chunk belongs to.
Short (16-bit) | The index of this chunk
3-bytes? (24-bit) | The position of the next chunk
byte (8-bit) | The type of file this chunk belongs to.

The succeeding 512 bytes are the raw data for the data chunk. A file in the file store is made up of many of these data chunks.
