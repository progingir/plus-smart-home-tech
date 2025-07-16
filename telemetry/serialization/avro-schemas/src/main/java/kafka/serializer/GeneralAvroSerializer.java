package kafka.serializer;

import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralAvroSerializer<T extends GenericContainer> implements Serializer<T> {
    private static final Logger log = LoggerFactory.getLogger(GeneralAvroSerializer.class);
    private static final int MAX_MESSAGE_SIZE = 1_048_576; // 1 MB

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            log.warn("Received null data for topic {}", topic);
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            log.debug("Serializing data for topic {}: {}", topic, data);
            DatumWriter<T> datumWriter = new SpecificDatumWriter<>(data.getSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            datumWriter.write(data, encoder);
            encoder.flush();
            byte[] result = outputStream.toByteArray();
            log.debug("Serialized data for topic {}, length: {} bytes", topic, result.length);
            if (result.length > MAX_MESSAGE_SIZE) {
                log.error("Serialized data for topic {} exceeds max size of {} bytes: {} bytes",
                        topic, MAX_MESSAGE_SIZE, result.length);
                throw new SerializationException("Serialized data too large for topic " + topic);
            }
            return result;
        } catch (IOException e) {
            log.error("Serialization error for topic {}: {}", topic, e.getMessage(), e);
            throw new SerializationException("Error serializing Avro message for topic " + topic, e);
        }
    }
}
