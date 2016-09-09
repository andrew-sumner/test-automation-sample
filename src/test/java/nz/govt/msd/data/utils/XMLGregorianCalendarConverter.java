package nz.govt.msd.data.utils;

import java.lang.reflect.Type;

import javax.xml.datatype.XMLGregorianCalendar;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nz.govt.msd.utils.ISODateTimeFormat;

public class XMLGregorianCalendarConverter {
	public static class Serializer implements JsonSerializer<Object> {
		public Serializer() {
			super();
		}

		public JsonElement serialize(Object t, Type type, JsonSerializationContext jsonSerializationContext) {
			XMLGregorianCalendar xgcal = (XMLGregorianCalendar) t;
			return new JsonPrimitive(xgcal.toXMLFormat());
		}
	}

	public static class Deserializer implements JsonDeserializer<Object> {

		@Override
		public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			String value = jsonElement.getAsString();

			try {
				if (value == null || value.isEmpty()) {
					return null;
				}

				return ISODateTimeFormat.toXMLGregorianCalendar(jsonElement.getAsString());
			} catch (Exception e) {
				throw new JsonParseException("Unable to parse XMLGregorianCalendar value '" + value + "'", e);
			}
		}
	}
}
