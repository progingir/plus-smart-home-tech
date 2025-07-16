package kafka.serializer;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    Encoder encoder;

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] result = null;
            encoder = EncoderFactory.get().binaryEncoder(baos, null);
            if (data != null) {
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
                writer.write(data, encoder);
                encoder.flush();
                result = baos.toByteArray();
            }
            return result;

        } catch (IOException e) {
            throw new SerializationException("Serialization error", e);
        }
    }

}
