package kafka.serializer;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class GeneralAvroDeserializer implements Deserializer<SpecificRecordBase> {
    private static final Logger log = LoggerFactory.getLogger(GeneralAvroDeserializer.class);

    @Override
    public SpecificRecordBase deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                log.warn("Received null or empty data for topic {}", topic);
                return null;
            }
            log.debug("Deserializing data for topic {}, length: {} bytes", topic, data.length);
            org.apache.avro.Schema schema = getSchemaForTopic(topic);
            log.debug("Using schema for topic {}: {}", topic, schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<SpecificRecordBase> reader = new SpecificDatumReader<>(schema);
            SpecificRecordBase result = reader.read(null, decoder);
            log.debug("Deserialized data for topic {}: {}", topic, result);
            return result;
        } catch (Exception e) {
            log.error("Deserialization error for topic {}, data length: {}", topic, data == null ? 0 : data.length, e);
            throw new SerializationException("Deserialization error for topic " + topic, e);
        }
    }

    private org.apache.avro.Schema getSchemaForTopic(String topic) {
        if ("telemetry.hubs.v1".equals(topic)) {
            return HubEventAvro.getClassSchema();
        } else if ("telemetry.sensors.v1".equals(topic)) {
            return SensorEventAvro.getClassSchema();
        }
        log.error("Unknown topic: {}", topic);
        throw new IllegalArgumentException("Unknown topic: " + topic);
    }
}