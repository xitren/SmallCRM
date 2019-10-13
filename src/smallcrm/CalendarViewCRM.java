/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smallcrm;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author xitre
 */
public class CalendarViewCRM extends CalendarView {

    Calendar db;
    Timer updateTimeThread;
    boolean editable;

    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(() -> {
                setToday(LocalDate.now());
                setTime(LocalTime.now());
            });
        }
    };

    public CalendarViewCRM(String name, String filename) {
        db = new Calendar(name);
        CalendarSource workCalendarSource = new CalendarSource("Work");
        workCalendarSource.getCalendars().addAll(db);
        getCalendarSources().setAll(workCalendarSource);
        loadFromFile(filename);
        this.setRequestedTime(LocalTime.now());
        updateTimeThread = new Timer("Calendar: Update Time Thread");
        updateTimeThread.schedule(timerTask, 0, 10000);
        this.showMonthPage();
    }

    public void saveToFile(String filename) {
        JSONObject calendar = new JSONObject();
        JSONArray list = new JSONArray();
        calendar.put("Events", list);
        calendar.put("Name", "Arsenty P. Gusev");

        Map<LocalDate, List<Entry<?>>> results = db.findEntries(
                LocalDate.of(2010, 1, 1),
                LocalDate.of(2100, 1, 1),
                ZoneId.systemDefault()
        );
        for (Map.Entry<LocalDate, List<Entry<?>>> pair : results.entrySet()) {
            for (Entry<?> item : pair.getValue()) {
                JSONObject obj = new JSONObject();
                obj.put("Start date year", item.getStartDate().getYear());
                obj.put("Start date month", item.getStartDate().getMonthValue());
                obj.put("Start date day", item.getStartDate().getDayOfMonth());
                obj.put("Start time hour", item.getStartTime().getHour());
                obj.put("Start time minute", item.getStartTime().getMinute());
                obj.put("End date year", item.getEndDate().getYear());
                obj.put("End date month", item.getEndDate().getMonthValue());
                obj.put("End date day", item.getEndDate().getDayOfMonth());
                obj.put("End time hour", item.getEndTime().getHour());
                obj.put("End time minute", item.getEndTime().getMinute());
                obj.put("All day", item.isFullDay());
                obj.put("Title", item.getTitle());
                obj.put("Place", item.getLocation());
                list.add(obj);
            }
        }

        try ( FileWriter file = new FileWriter(filename)) {
            file.write(calendar.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(calendar);
    }

    private void loadFromFile(String filename) {
        JSONParser parser = new JSONParser();
        try ( Reader reader = new FileReader(filename)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray list = (JSONArray) jsonObject.get("Events");
            Iterator<JSONObject> iterator = list.iterator();
            while (iterator.hasNext()) {
                Long sd_y, sd_m, sd_d, sd_h, sd_mm;
                Long ed_y, ed_m, ed_d, ed_h, ed_mm;
                String descr;
                String place;
                JSONObject item = iterator.next();
                sd_y = (Long) item.get("Start date year");
                sd_m = (Long) item.get("Start date month");
                sd_d = (Long) item.get("Start date day");
                sd_h = (Long) item.get("Start time hour");
                sd_mm = (Long) item.get("Start time minute");
                ed_y = (Long) item.get("End date year");
                ed_m = (Long) item.get("End date month");
                ed_d = (Long) item.get("End date day");
                ed_h = (Long) item.get("End time hour");
                ed_mm = (Long) item.get("End time minute");
                descr = (String) item.get("Title");
                place = (String) item.get("Place");
                Entry<?> nitem = new Entry(descr,
                        new Interval(
                                LocalDate.of(
                                        sd_y.intValue(), sd_m.intValue(),
                                        sd_d.intValue()
                                ),
                                LocalTime.of(sd_h.intValue(), sd_mm.intValue()),
                                LocalDate.of(
                                        ed_y.intValue(), ed_m.intValue(),
                                        ed_d.intValue()
                                ),
                                LocalTime.of(ed_h.intValue(), ed_mm.intValue())
                        )
                );
                nitem.setFullDay((Boolean) item.get("All day"));
                nitem.setLocation(place);
                db.addEntry(nitem);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Set<Entry<?>> studies_on_db = new HashSet();
    private final static Alert alert = new Alert(Alert.AlertType.ERROR);
    
    public Set<Entry<?>> getEntriesSet() {
        Map<LocalDate, List<Entry<?>>> currentEntries = db.findEntries(
                LocalDate.of(2010, 1, 1),
                LocalDate.of(2100, 1, 1),
                ZoneId.systemDefault()
        );
        Set<Entry<?>> currentEntriesSet = new HashSet();
	currentEntries.entrySet().forEach((pair) -> {
	    pair.getValue().forEach((item) -> {
		currentEntriesSet.add(item);
	    });
	});
	return currentEntriesSet;
    }

    @Override
    protected void finalize() throws Throwable {
        updateTimeThread.cancel();
        //super.finalize(); see alip's comment on why this should not be invoked.
    }
}





