package com.aiotico.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

	@Bean
	public KStream<String, String> locationStream(StreamsBuilder builder) {
		KStream<String, String> stream = builder.stream("vts.location",
				Consumed.with(Serdes.String(), Serdes.String()));

		stream.peek((key, value) -> System.out.println("Received: " + value))
				.filter((key, value) -> isOverspeed(value) || isOutsideGeofence(value)).to("vts.alerts");

		return stream;
	}

	private boolean isOverspeed(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return obj.getDouble("speed") > 80;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isOutsideGeofence(String json) {
		try {
			JSONObject obj = new JSONObject(json);

			double lat = obj.getDouble("lat");
			double lon = obj.getDouble("lon");
			return !(lat >= 25.0 && lat <= 26.0 && lon >= 81.0 && lon <= 82.0);
		} catch (Exception e) {
			return false;
		}
	}

}
