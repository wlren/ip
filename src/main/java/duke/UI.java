package duke;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main logic of Duke chatbot on responding to command and showing output to user.
 */
public class UI {
    private final Parser parser;
    private final Storage storage;
    private final TaskList taskList;

    /**
     * Fields for commands to be executed.
     *
     * @param parser
     * @param storage
     * @param taskList
     */
    public UI(Parser parser, Storage storage, TaskList taskList) {
        this.parser = parser;
        this.storage = storage;
        this.taskList = taskList;
    }

    /**
     * Main method to be constantly taking in commands until "bye".
     * 
     * @throws DukeException
     */
    public String start(CommandType command) throws DukeException{

        int taskIndex;
        String searchTerm;
        Task newTask;

        switch (command) {
        case Exit:
            try {
                storage.writeData();
            } catch (IOException e) {
                return "Error saving file";
            }
            return exitMsg();
        case List:
            return listMsg();
        case DeleteTask:
            taskIndex = parser.getIndex();
            taskList.deleteTask(taskIndex);
            return deleteMsg(taskIndex);
        case MarkAsDone:
            taskIndex = parser.getIndex();
            return taskList.markAsDone(taskIndex);
        case AddToDo:
        case AddEvent:
        case AddDeadline:
            try {
                newTask = parser.generateTask();
                taskList.addTask(newTask);
                return addTaskMsg(newTask);
            } catch (DukeException e) {
                return e.getMessage();
            }
        case Find:
            searchTerm = parser.getSearchTerm();
            ArrayList<Task> matchingTasks = new ArrayList<>();
            for (Task task : taskList.getTasks()) {
                if (task.description.contains(searchTerm)) {
                    matchingTasks.add(task);
                }
            }
            return findMsg(matchingTasks);
        case Error:
            throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        default:
            return "Command error encountered, please inform Duke maintainers!";
        }
    }

    private String addTaskMsg(Task newTask) {
        return "Got it. I've added this task: \n" +
                newTask.toString() + "\n" +
                String.format("Now you have %d tasks in the list.%n", taskList.getListSize());
    }

    private String exitMsg() {
        return " Bye. Hope to see you again soon!";
    }


    private String listMsg() {
        StringBuilder response = new StringBuilder("  Here are the tasks in your list:\n");
        for (int i = 1; i < taskList.getListSize() + 1; i++) {
            response.append(String.format("  %d. %s%n", i, taskList.getTaskString(i - 1)));
        }
        return response.toString();
    }

    private String findMsg(ArrayList<Task> tasks) {
        StringBuilder response = new StringBuilder("  Here are the matching tasks in your list:\n");
        for (int i = 1; i < tasks.size() + 1; i++) {
            response.append(String.format("  %d. %s%n", i, tasks.get(i - 1).toString()));
        }
        return response.toString();
    }

    private String deleteMsg(int index) {
        return "  Noted. I've removed this task:\n" +
                "\n" + taskList.getTaskString(index) + "\n" +
                String.format("Now you have %d tasks in the list.%n", taskList.getListSize() - 1);
    }

}
