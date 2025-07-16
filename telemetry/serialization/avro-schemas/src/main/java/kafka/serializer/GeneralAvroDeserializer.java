package kafka.serializer;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class GeneralAvroDeserializer implements Deserializer<SpecificRecordBase> {
    @Override
    public SpecificRecordBase deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<SpecificRecordBase> reader = new SpecificDatumReader<>(getSchemaForTopic(topic));
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException("Deserialization error for topic " + topic, e);
        }
    }

    private org.apache.avro.Schema getSchemaForTopic(String topic) {
        if ("telemetry.hubs.v1".equals(topic)) {
            return HubEventAvro.getClassSchema();
        } else if ("telemetry.sensors.v1".equals(topic)) {
            return SensorEventAvro.getClassSchema();
        }
        throw new IllegalArgumentException("Unknown topic: " + topic);
    }
}