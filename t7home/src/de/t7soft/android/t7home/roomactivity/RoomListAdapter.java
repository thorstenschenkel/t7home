package de.t7soft.android.t7home.roomactivity;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;

public class RoomListAdapter extends BaseAdapter {

	private static final int TYPE_UNKOWN = -1;
	private static final int TYPE_TEMPERATURE_HUMIDITY_DEVICE = 0;
	private static final int TYPE_MAX_COUNT = 1;
	private static final Format TEMPERATURE_FORMAT = new DecimalFormat("#.#");
	private static final Format HUMIDITY_FORMAT = new DecimalFormat("#.#");

	private final Context context;
	private final List<Object> listItems;
	private final LayoutInflater inflater;

	public RoomListAdapter(Context context, List<Object> listItems) {
		this.context = context;
		this.listItems = listItems;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) instanceof TemperatureHumidityDevice) {
			return TYPE_TEMPERATURE_HUMIDITY_DEVICE;
		} else {
			return TYPE_UNKOWN;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		int type = getItemViewType(position);
		if (rowView == null) {
			switch (type) {
				case TYPE_TEMPERATURE_HUMIDITY_DEVICE:
					rowView = inflater.inflate(R.layout.temperature_humidity_device_row, null);
					break;
			}
		}
		if (rowView != null) {
			switch (type) {
				case TYPE_TEMPERATURE_HUMIDITY_DEVICE:
					TemperatureHumidityDevice temperatureHumidityDevice = (TemperatureHumidityDevice) listItems
							.get(position);
					updateTemperatureHumidityDeviceRow(rowView, temperatureHumidityDevice);
					break;
			}
		}
		return rowView;
	}

	private void updateTemperatureHumidityDeviceRow(View rowView, TemperatureHumidityDevice temperatureHumidityDevice) {

		TextView textView = (TextView) rowView.findViewById(R.id.textViewRoomTemperatureValue);
		double doubleValue = temperatureHumidityDevice.getTemperatureSensor().getTemperature();
		String value = TEMPERATURE_FORMAT.format(doubleValue) + "°C";
		textView.setText(value);

		textView = (TextView) rowView.findViewById(R.id.textViewRoomHumidityValue);
		doubleValue = temperatureHumidityDevice.getRoomHumiditySensor().getHumidity();
		value = HUMIDITY_FORMAT.format(doubleValue) + "%";
		textView.setText(value);

	}

}
