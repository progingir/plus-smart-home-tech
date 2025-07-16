package kafka.serializer;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    private static final Logger log = LoggerFactory.getLogger(GeneralAvroSerializer.class);

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            log.warn("Received null data for topic {}", topic);
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
            log.info("Serializing data for topic {}: {}", topic, data);
            writer.write(data, encoder);
            encoder.flush();
            byte[] result = baos.toByteArray();
            log.info("Serialized data length: {}", result.length);
            return result;
        } catch (IOException e) {
            log.error("Serialization error for topic {}", topic, e);
            throw new SerializationException("Serialization error for topic " + topic, e);
        }
    }
}
