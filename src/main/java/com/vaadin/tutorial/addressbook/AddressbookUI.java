package com.vaadin.tutorial.addressbook;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HasValue;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tutorial.addressbook.backend.TodoTask;
import com.vaadin.tutorial.addressbook.backend.TodoTaskService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.ui.TextField;

/* User Interface written in Java.
 *
 * Define the user interface shown on the Vaadin generated web page by extending the UI class.
 * By default, a new UI instance is automatically created when the page is loaded. To reuse
 * the same instance, add @PreserveOnRefresh.
 */
@Title("Addressbook")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class AddressbookUI extends UI {

    /*
     * Hundreds of widgets. Vaadin's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default Vaadin components are in the
     * com.vaadin.ui package and there are over 500 more in
     * vaadin.com/directory.
     */
    TextField filter = new TextField();
    Grid taskList = new Grid();
    Button newTask = new Button("New Task");

    // TodoTaskForm is an example of a custom component class
    TodoTaskForm todoTaskForm = new TodoTaskForm();

    // TodoTaskService is a in-memory mock DAO that mimics
    // a real-world datasource. Typically implemented for
    // example as EJB or Spring Data based service.
    TodoTaskService service = TodoTaskService.createDemoService();

    /*
     * The "Main method".
     *
     * This is the entry point method executed to initialize and configure the
     * visible user interface. Executed on every browser reload because a new
     * instance is created for each web page loaded.
     */
    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        /*
         * Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. Vaadin automatically sends only
         * the needed changes to the web page without loading a new page.
         *
         * Aesthetic change to push
         */
        newTask.addClickListener(e -> todoTaskForm.edit(new TodoTask()));

        filter.setPlaceholder("Filter tasks...");
        /** Travis seems to be having issues with this lambda.  */
        filter.addValueChangeListener((HasValue.ValueChangeListener<? super String>) valueChangeEvent -> refreshTasks(valueChangeEvent.getValue()));

        taskList.setContainerDataSource(new BeanItemContainer<>(TodoTask.class));
        taskList.setColumnOrder("firstName", "lastName", "taskNotes", "startDate", "endDate");
        taskList.removeColumn("id");
        taskList.removeColumn("subject");
        taskList.setSelectionMode(Grid.SelectionMode.SINGLE);
        taskList.addSelectionListener(
                e -> todoTaskForm.edit((TodoTask) taskList.getSelectedRow())
        );
        refreshTasks();
    }

    /*
     * Robust layouts.
     *
     * Layouts are components that contain other components. HorizontalLayout
     * contains TextField and Button. It is wrapped with a Grid into
     * VerticalLayout for the left side of the screen. Allow user to resize the
     * components with a SplitPanel.
     *
     * In addition to programmatically building layout in Java, you may also
     * choose to setup layout declaratively with Vaadin Designer, CSS and HTML.
     */
    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newTask);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, taskList);
        left.setSizeFull();
        taskList.setSizeFull();
        left.setExpandRatio(taskList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, todoTaskForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        setContent(mainLayout);
    }

    /*
     * Choose the design patterns you like.
     *
     * It is good practice to have separate data access methods that handle the
     * back-end access and/or the user interface updates. You can further split
     * your code into classes to easier maintenance. With Vaadin you can follow
     * MVC, MVP or any other design pattern you choose.
     */
    void refreshTasks() {
        refreshTasks(filter.getValue());
    }

    private void refreshTasks(String stringFilter) {
        taskList.setContainerDataSource(new BeanItemContainer<>(
                TodoTask.class, service.findAll(stringFilter)));
        todoTaskForm.setVisible(false);
    }

    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = AddressbookUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
