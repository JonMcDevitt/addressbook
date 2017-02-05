package com.vaadin.tutorial.addressbook.backend;

import org.apache.commons.beanutils.BeanUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Separate Java service class.
 * Backend implementation for the address book application, with "detached entities"
 * simulating real world DAO. Typically these something that the Java EE
 * or Spring backend services provide.
 */
// Backend service class. This is just a typical Java backend implementation
// class and nothing Vaadin specific.
public class TodoTaskService {

    // Create dummy data by randomly combining first and last names
    private static String[] subjectLines = {
            "Note 1", "Note 2", "Note 3", "Note 4", "Note 5", "Note 6", "Note 7", "Note 8", "Note 9", "Note 10"
    };

    private static String[] taskNotes = {
            " - take out trash", " - cut wooden beams for shed", " - install an IDE", " - water plants",
            " - take stock of fridge for grocery list", " - clean bathrooms", " - create GitHub repo for project",
            " - create JIRA repo for project", " - set up Travis-CI", " - configure Heroku"
    };

    private static String[] fnames = { "Peter", "Alice", "John", "Mike", "Olivia",
            "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene", "Lisa",
            "Linda", "Timothy", "Daniel", "Brian", "George", "Scott",
            "Jennifer" };
    private static String[] lnames = { "Smith", "Johnson", "Williams", "Jones",
            "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Young", "King", "Robinson" };

    private static TodoTaskService instance;

    public static TodoTaskService createDemoService() {
        if (instance == null) {

            final TodoTaskService todoTaskService = new TodoTaskService();

            Calendar cal = Calendar.getInstance();
            Random r = new Random(0);
            for (int i = 0; i < 10; i++) {
                TodoTask todoTask = new TodoTask();

                todoTask.setFirstName(fnames[r.nextInt(fnames.length)]);
                todoTask.setLastName(lnames[r.nextInt(fnames.length)]);
                todoTask.setStartDate(cal.getTime());
                todoTask.setEndDate(cal.getTime());
                todoTask.setId((long) (i + 100));
                todoTask.setSubject(subjectLines[i]);
                todoTask.setTaskNotes(taskNotes[i]);

                todoTaskService.save(todoTask);
            }
            instance = todoTaskService;
        }

        return instance;
    }

    private HashMap<Long, TodoTask> tasks = new HashMap<>();
    private long nextId = 0;

    public synchronized List<TodoTask> findAll(String stringFilter) {
        ArrayList arrayList = new ArrayList();
        for (TodoTask todoTask : tasks.values()) {
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || todoTask.toString().toLowerCase()
                                .contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(todoTask.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(TodoTaskService.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
        arrayList.sort((Comparator<TodoTask>) (o1, o2) -> (int) (o1.getId() - o2.getId()));
        return arrayList;
    }

    public synchronized long count() {
        return tasks.size();
    }

    public synchronized void delete(TodoTask value) {
        tasks.remove(value.getId());
    }

    public synchronized void save(TodoTask entry) {
        if (entry.getId() == null) {
            entry.setId(nextId++);
        }
        try {
            entry = (TodoTask) BeanUtils.cloneBean(entry);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        tasks.put(entry.getId(), entry);
    }

}
