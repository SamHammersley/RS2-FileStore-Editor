package rs2.filestore.editor.cache.fs.index;

import rs2.filestore.editor.cache.fs.FileStore;
import rs2.filestore.editor.cache.fs.index.entry.IndexEntry;
import rs2.filestore.editor.io.ReadOnlyBuffer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for decoding JaGeX format indices.
 */
public final class IndexDecoder {

    /**
     * The size of an index entry in bytes.
     */
    private static final int INDEX_ENTRY_SIZE = 6;

    /**
     * The {@link ReadOnlyBuffer} containing all the file data for a {@link FileStore}
     */
    private final ReadOnlyBuffer dataBuffer;

    public IndexDecoder(ReadOnlyBuffer dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    /**
     * Calls {@link #decode(int, ReadOnlyBuffer)} with the specified {@link Path}, as a {@link ReadOnlyBuffer}.
     *
     * @param path the path to the index file.
     * @return an {@link Index} decoded from the index file and {@link #dataBuffer}.
     */
    public Index decode(Path path) {
        String extension = path.getFileName().toString();
        int id = Integer.parseInt(extension.substring(extension.length() - 1));

        return decode(id, ReadOnlyBuffer.fromPath(path));
    }

    /**
     * Decodes an index from the given buffer.
     *
     * @param indexId the id of the index to decode.
     * @param indexBuffer the buffer containing index data.
     * @return an instance of {@link Index}.
     */
    public Index decode(int indexId, ReadOnlyBuffer indexBuffer) {
        final List<IndexEntry> entries = new ArrayList<>();

        int totalSize = 0;

        for (int fileId = 0; indexBuffer.hasRemainingBytes(INDEX_ENTRY_SIZE); fileId++) {

            final int fileSize = indexBuffer.getUnsigned24BitInt();
            final int initialChunkId = indexBuffer.getUnsigned24BitInt();

            if (initialChunkId <= 0 || initialChunkId > dataBuffer.length() / DataChunk.DATA_CHUNK_BODY_SIZE) {
                entries.add(IndexEntry.EMPTY_ENTRY);
                continue;
            }

            List<DataChunk> entryData = new ArrayList<>((fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1);

            for (int chunkId = 0, currentChunkIndex = initialChunkId; chunkId < (fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1; chunkId++) {
                dataBuffer.seek(currentChunkIndex * DataChunk.DATA_CHUNK_SIZE);

                DataChunk dataChunk = DataChunk.decode(dataBuffer, fileSize, fileId, chunkId);

                entryData.add(dataChunk);

                final int nextChunkId = dataChunk.getNextChunkId();
                if (nextChunkId == 0) {
                    break;
                }

                currentChunkIndex = nextChunkId;
            }
            totalSize += fileSize;

            entries.add(new IndexEntry(indexId, fileId, fileSize, initialChunkId, entryData));
        }

        return new Index(indexId, entries, totalSize);
    }

}