package de.t7soft.android.t7home.roomactivity;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RollerShutterActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

/*
 * setOnSeekBarChangeListener
 * 
 */
public class RoomListAdapter extends BaseAdapter {

	private static final int TYPE_UNKOWN = -1;
	private static final int TYPE_TEMPERATURE_HUMIDITY_DEVICE = 0;
	private static final int TYPE_WINDOW_DOOR_SENSOR = 1;
	private static final int TYPE_DAY_SENSOR = 2;
	private static final int TYPE_ROLLER_SHUTTER = 3;
	private static final int TYPE_MAX_COUNT = 4;
	private static final Format TEMPERATURE_FORMAT = new DecimalFormat("#.#");
	private static final Format HUMIDITY_FORMAT = new DecimalFormat("#.#");
	private static final Format TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private final Context context;
	private final List<Object> listItems;
	private final ActuatorChangeListener changeListener;
	private final LayoutInflater inflater;

	public RoomListAdapter(final Context context, final List<Object> listItems,
			final ActuatorChangeListener changeListener) {
		this.context = context;
		this.listItems = listItems;
		this.changeListener = changeListener;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(final int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public int getItemViewType(final int position) {
		if (getItem(position) instanceof TemperatureHumidityDevice) {
			return TYPE_TEMPERATURE_HUMIDITY_DEVICE;
		} else if (getItem(position) instanceof WindowDoorSensor) {
			return TYPE_WINDOW_DOOR_SENSOR;
		} else if (getItem(position) instanceof DaySensor) {
			return TYPE_DAY_SENSOR;
		} else if (getItem(position) instanceof RollerShutterActuator) {
			return TYPE_ROLLER_SHUTTER;
		} else {
			return TYPE_UNKOWN;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View rowView = convertView;
		final int type = getItemViewType(position);
		if (rowView == null) {
			switch (type) {
				case TYPE_TEMPERATURE_HUMIDITY_DEVICE:
					rowView = inflater.inflate(R.layout.temperature_humidity_device_row, null);
					break;
				case TYPE_WINDOW_DOOR_SENSOR:
					rowView = inflater.inflate(R.layout.window_door_sensor_row, null);
					break;
				case TYPE_DAY_SENSOR:
					rowView = inflater.inflate(R.layout.day_sensor_row, null);
					break;
				case TYPE_ROLLER_SHUTTER:
					rowView = inflater.inflate(R.layout.roller_shutter_row, null);
					break;
				default:
					rowView = inflater.inflate(R.layout.dummy_device_row, null);
					break;
			}
		}
		if (rowView != null) {
			switch (type) {
				case TYPE_TEMPERATURE_HUMIDITY_DEVICE:
					final TemperatureHumidityDevice temperatureHumidityDevice = (TemperatureHumidityDevice) listItems
							.get(position);
					updateRow(rowView, temperatureHumidityDevice);
					break;
				case TYPE_WINDOW_DOOR_SENSOR:
					final WindowDoorSensor windowDoorSensor = (WindowDoorSensor) listItems.get(position);
					updateRow(rowView, windowDoorSensor);
					break;
				case TYPE_DAY_SENSOR:
					final DaySensor daySensor = (DaySensor) listItems.get(position);
					updateRow(rowView, daySensor);
					break;
				case TYPE_ROLLER_SHUTTER:
					final RollerShutterActuator rollerShutter = (RollerShutterActuator) listItems.get(position);
					updateRow(rowView, rollerShutter);
					break;
				default:
					// nothing to do
					final TextView textViewDummyLabel = (TextView) rowView.findViewById(R.id.textViewDummyLabel);
					final Object listItem = listItems.get(position);
					if (listItem instanceof LogicalDevice) {
						final String deviceType = ((LogicalDevice) listItem).getType();
						if ((deviceType != null) && !deviceType.isEmpty()) {
							textViewDummyLabel.setText(deviceType);
						}
					}
					break;
			}
		}
		return rowView;
	}

	private void updateRow(final View rowView, final WindowDoorSensor sensor) {

		final TextView textView = (TextView) rowView.findViewById(R.id.textViewDeviceName);
		final String value = sensor.getDeviceName();
		textView.setText(value);

		final Switch lockedSwitch = (Switch) rowView.findViewById(R.id.switchOpen);
		final Boolean isOpen = sensor.isOpen();
		lockedSwitch.setChecked(isOpen);

	}

	private void updateRow(final View rowView, final DaySensor sensor) {

		TextView textView = (TextView) rowView.findViewById(R.id.textViewSunriseValue);
		Date dateValue = sensor.getNextSunrise();
		String value = null;
		if (dateValue != null) {
			try {
				value = TIME_FORMAT.format(dateValue);
				value = MessageFormat.format(context.getString(R.string.time_value), value);
				textView.setText(value);
			} catch (Exception e) {
			}
		}
		if (value == null) {
			textView.setVisibility(View.GONE);
		}

		textView = (TextView) rowView.findViewById(R.id.textViewSunsetValue);
		dateValue = sensor.getNextSunset();
		value = null;
		if (dateValue != null) {
			try {
				value = TIME_FORMAT.format(dateValue);
				value = MessageFormat.format(context.getString(R.string.time_value), value);
				textView.setText(value);
			} catch (Exception e) {
			}
		}
		if (value == null) {
			textView.setVisibility(View.GONE);
		}

	}

	private void updateRow(final View rowView, final RollerShutterActuator rollerShutter) {

		TextView textView = (TextView) rowView.findViewById(R.id.textViewShutterNameLabel);
		final String name = rollerShutter.getDeviceName();
		if ((name != null) && (name.length() > 0)) {
			textView.setText(name);
		}

		textView = (TextView) rowView.findViewById(R.id.textViewShutterLevelValue);
		final int intValue = rollerShutter.getShutterLevel();
		final String value = intValue + " %";
		textView.setText(value);

		final SeekBar shutterLevelSeekBar = (SeekBar) rowView.findViewById(R.id.seekBarShutterLevel);
		// shutterLevelSeekBar.setEnabled(!isLocked);
		shutterLevelSeekBar.setMax(0);
		shutterLevelSeekBar.setMax(100);
		shutterLevelSeekBar.setProgress(rollerShutter.getShutterLevel());

		shutterLevelSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				if (fromUser) {
					fireChanged(rollerShutter, progress);
				}
			}
		});

	}

	private void updateRow(final View rowView, final TemperatureHumidityDevice temperatureHumidityDevice) {

		TextView textView = (TextView) rowView.findViewById(R.id.textViewRoomTemperatureValue);
		double doubleValue = temperatureHumidityDevice.getTemperatureSensor().getTemperature();
		String value = TEMPERATURE_FORMAT.format(doubleValue) + "�C";
		textView.setText(value);

		textView = (TextView) rowView.findViewById(R.id.textViewRoomHumidityValue);
		doubleValue = temperatureHumidityDevice.getRoomHumiditySensor().getHumidity();
		value = HUMIDITY_FORMAT.format(doubleValue) + " %";
		textView.setText(value);

		final Switch lockedSwitch = (Switch) rowView.findViewById(R.id.switchLocked);
		final Boolean isLocked = temperatureHumidityDevice.getTemperatureActuator().getIsLocked();
		lockedSwitch.setChecked(isLocked);

		textView = (TextView) rowView.findViewById(R.id.textViewPresetTemperatureValue);
		final double presetTemperature = temperatureHumidityDevice.getTemperatureActuator().getPointTemperature();
		value = TEMPERATURE_FORMAT.format(presetTemperature) + "�C";
		textView.setText(value);

		textView = (TextView) rowView.findViewById(R.id.textViewPresetMinTemperature);
		final double minTemperature = temperatureHumidityDevice.getTemperatureActuator().getMinTemperature();
		value = TEMPERATURE_FORMAT.format(minTemperature) + "�C";
		textView.setText(value);

		textView = (TextView) rowView.findViewById(R.id.textViewPresetMaxTemperature);
		final double maxTemperature = temperatureHumidityDevice.getTemperatureActuator().getMaxTemperature();
		value = TEMPERATURE_FORMAT.format(maxTemperature) + "�C";
		textView.setText(value);

		final SeekBar temperatureSeekBar = (SeekBar) rowView.findViewById(R.id.seekBarPresetTemperature);
		temperatureSeekBar.setEnabled(!isLocked);
		final long max = Math.round((maxTemperature * 10) - (minTemperature * 10));
		temperatureSeekBar.setMax(0);
		temperatureSeekBar.setMax((int) max);
		final long progress = Math.round((presetTemperature * 10) - (minTemperature * 10));
		temperatureSeekBar.setProgress((int) progress);

		temperatureSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// nothing to do
			}

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				if (fromUser) {
					fireChanged(temperatureHumidityDevice, minTemperature, progress);
				}
			}
		});

		lockedSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				fireChanged(temperatureHumidityDevice, minTemperature, progress);
			}

		});

	}

	private void fireChanged(final TemperatureHumidityDevice temperatureHumidityDevice, final double minTemperature,
			final long progress) {
		double newTemperatue = progress / 10.0;
		newTemperatue += minTemperature;
		final RoomTemperatureActuator temperatureActuator = temperatureHumidityDevice.getTemperatureActuator();
		final String value = TEMPERATURE_FORMAT.format(newTemperatue);
		changeListener.changed(temperatureActuator, value);
	}

	private void fireChanged(final RollerShutterActuator rollerShutter, final int level) {
		final String value = Integer.toString(level);
		changeListener.changed(rollerShutter, value);
	}

}
