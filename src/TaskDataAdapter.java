import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class TaskDataAdapter implements JsonSerializer<TaskData>, JsonDeserializer<TaskData> {
    @Override
    public JsonElement serialize(TaskData task, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("taskName", task.getTaskName());
        json.addProperty("taskTime", task.getTaskTime());
        json.addProperty("startDate", task.getStartDate().toString());
        json.addProperty("recurrence", task.getRecurrence());

        if (task instanceof FoodTask) {
            FoodTask food = (FoodTask) task;

            json.addProperty("taskType", "FOOD");
            json.addProperty("animalSpecies", food.getAnimalSpecies());
            json.addProperty("enclosureZone", food.getEnclosureZone());
            json.addProperty("dietNotes", food.getDietNotes());

        }

        else if (task instanceof MedicineTask) {
            MedicineTask medicine = (MedicineTask) task;

            json.addProperty("taskType", "MEDICINE");
            json.addProperty("animalID", medicine.getAnimalID());
            json.addProperty("dosage", medicine.getDosage());
            json.addProperty("administeredBy", medicine.getAdministeredBy());

        }

        else if (task instanceof MiscellaneousTask) {
            MiscellaneousTask misc = (MiscellaneousTask) task;

            json.addProperty("taskType", "MISC");
            json.addProperty("habitatLocation", misc.getHabitatLocation());
            json.addProperty("safetyRiskLevel", misc.getSafetyRiskLevel());
            json.addProperty("equipmentNeeded", misc.getEquipmentNeeded());

        }

        else {
            json.addProperty("taskType", "GENERAL");
        }

        return json;
    }

    @Override
    public TaskData deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        JsonObject json = element.getAsJsonObject();

        String taskName = json.get("taskName").getAsString();
        String taskTime = json.get("taskTime").getAsString();

        LocalDate startDate = LocalDate.parse(json.get("startDate").getAsString());

        String recurrence = json.get("recurrence").getAsString();

        String taskType = json.get("taskType").getAsString();

        switch (taskType) {
            case "FOOD":
                return new FoodTask(
                        taskName,
                        taskTime,
                        startDate,
                        recurrence,
                        json.get("animalSpecies").getAsString(),
                        json.get("enclosureZone").getAsString(),
                        json.get("dietNotes").getAsString()
                );

            case "MEDICINE":
                return new MedicineTask(
                        taskName,
                        taskTime,
                        startDate,
                        recurrence,
                        json.get("animalID").getAsString(),
                        json.get("dosage").getAsString(),
                        json.get("administeredBy").getAsString()
                );

            case "MISC":
                return new MiscellaneousTask(
                        taskName,
                        taskTime,
                        startDate,
                        recurrence,
                        json.get("habitatLocation").getAsString(),
                        json.get("safetyRiskLevel").getAsString(),
                        json.get("equipmentNeeded").getAsString()
                );

            case "GENERAL":
                return new TaskData(
                        taskName,
                        taskTime,
                        startDate,
                        recurrence
                );

            default:
                throw new JsonParseException("Unknown task type: " + taskType);
        }
    }
}