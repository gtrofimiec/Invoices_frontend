package com.myprojects.invoices_frontend.layout.forms;

import com.myprojects.invoices_frontend.MainView;
import com.myprojects.invoices_frontend.config.converters.StringToCustomerConverter;
import com.myprojects.invoices_frontend.config.converters.StringToUserConverter;
import com.myprojects.invoices_frontend.domain.Invoices;
import com.myprojects.invoices_frontend.services.CustomersService;
import com.myprojects.invoices_frontend.services.InvoicesService;
import com.myprojects.invoices_frontend.services.UsersService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.converter.StringToDateConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jetbrains.annotations.NotNull;

public class InvoicesForm extends FormLayout {

    @PropertyId("number")
    private TextField txtNumber = new TextField("Numer faktury");
    @PropertyId("date")
    private TextField txtDate = new TextField("Data wystawienia");
    @PropertyId("user")
    private TextField txtUser = new TextField("Sprzedawca");
    @PropertyId("customer")
    private TextField txtCustomer = new TextField("Kontrahent");
    @PropertyId("netSum")
    private BigDecimalField txtNetSum = new BigDecimalField("Wartość netto");
    @PropertyId("vatSum")
    private BigDecimalField txtVatSum = new BigDecimalField("Wartość VAT");
    @PropertyId("grossSum")
    private BigDecimalField txtGrossSum = new BigDecimalField("Wartość brutto");
    @PropertyId("paymentMethod")
    private TextField txtPayment = new TextField("Forma płatności");
    private Button btnSave = new Button("Zapisz");
    private Button btnDelete = new Button("Usuń");
    private Button btnCancel = new Button("Zamknij");
    private Button btnChangeCustomer = new Button("Zmień ...");
    public Button btnSelectCustomer = new Button("Wybierz ...");
    @PropertyId("productsList")
    private Grid<Invoices> gridProductsList = new Grid<>(Invoices.class);
    private Binder<Invoices> binder = new Binder<>(Invoices.class);
    private MainView mainView;
    private InvoicesService invoicesService = InvoicesService.getInstance();
    private UsersService usersService = UsersService.getInstance();
    private CustomersService customersService = CustomersService.getInstance();

    public InvoicesForm(@NotNull MainView mainView) {
        this.mainView = mainView;

        mainView.txtInvoicesFilter.setPlaceholder("Filtruj po numerze ...");
        mainView.txtInvoicesFilter.setClearButtonVisible(true);
        mainView.txtInvoicesFilter.setValueChangeMode(ValueChangeMode.EAGER);

        gridProductsList.removeAllColumns();
        gridProductsList.addColumn(Invoices::getProductsList).setHeader("Nazwa");

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnCancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnChangeCustomer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        txtUser.setEnabled(false);

        mainView.txtInvoicesFilter.addValueChangeListener(e -> find());
        btnSave.addClickListener(event -> updateInvoice());
        btnDelete.addClickListener(event -> deleteInvoice());
        btnCancel.addClickListener(event -> cancel());
        btnChangeCustomer.addClickListener(event -> changeCustomer());

        HorizontalLayout buttons = new HorizontalLayout(btnSave, btnDelete, btnCancel);
        VerticalLayout userLayout = new VerticalLayout(txtUser);
        VerticalLayout customerLayout = new VerticalLayout(txtCustomer, btnChangeCustomer);
        VerticalLayout productsList = new VerticalLayout(gridProductsList);

        add(txtNumber, txtDate, userLayout, customerLayout, txtNetSum, txtVatSum, txtGrossSum, txtPayment,
                productsList, buttons);

        binder.forField(txtCustomer)
                .withNullRepresentation("")
                .withConverter(new StringToCustomerConverter())
                .bind(Invoices::getCustomer, Invoices::setCustomer);
        binder.forField(txtUser)
                .withNullRepresentation("")
                .withConverter(new StringToUserConverter())
                .bind(Invoices::getUser, Invoices::setUser);
        binder.forField(txtDate)
                .withNullRepresentation("")
                .withConverter(new StringToDateConverter())
                .bind(Invoices::getDate, Invoices::setDate);
        binder.bindInstanceFields(this);
    }

    private void updateInvoice() {
        Invoices invoice = binder.getBean();
        if(!invoice.getNumber().isEmpty()) {
            invoicesService.updateInvoice(invoice);
            mainView.gridInvoices.setItems(invoicesService.getInvoicesList());
        }
        this.setVisible(false);
    }

    private void deleteInvoice() {
        Invoices invoice = binder.getBean();
        invoicesService.deleteInvoice(invoice);
        this.setVisible(false);
        mainView.gridInvoices.setItems(invoicesService.getInvoicesList());
    }

    private void cancel() {
        this.setVisible(false);
    }

    public void updateInvoicesForm(Invoices invoice) {
        if (invoice == null) {
            setVisible(false);
        } else {
            binder.setBean(invoice);
            setVisible(true);
            txtNumber.focus();
        }
    }

    private void changeCustomer() {
        mainView.itemsToolbar.add(btnSelectCustomer);
        mainView.mainContent.remove(mainView.gridInvoices);
        mainView.mainContent.remove(this);
        mainView.mainContent.add(mainView.gridSelectCustomer);
        mainView.gridSelectCustomer.setItems(customersService.getCustomersList());
        btnSelectCustomer.setVisible(true);
        mainView.gridSelectCustomer.setVisible(true);
        btnSelectCustomer.addClickListener(e -> selectCustomer());
        mainView.gridSelectCustomer.asSingleSelect().addValueChangeListener(event -> selectCustomer());
    }

    private void selectCustomer() {
        txtCustomer.setValue(mainView.gridSelectCustomer.asSingleSelect().getValue().getFullName());
        mainView.itemsToolbar.remove(btnSelectCustomer);
        mainView.mainContent.remove(mainView.gridSelectCustomer);
        mainView.mainContent.add(mainView.gridInvoices, this);
        mainView.gridInvoices.setVisible(true);
        this.setVisible(true);
    }

    private void find() {
        mainView.gridInvoices.setItems(
                invoicesService.findByNumber(mainView.txtInvoicesFilter.getValue())
        );
    }
}