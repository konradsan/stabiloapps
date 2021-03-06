package ru.kit.stabiloapps.dynamicTest;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.kit.SoundManager;
import ru.kit.stabiloapps.dynamicTest.controller.DynamicTestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by mikha on 23.01.2017.
 */
public class DynamicTestStage extends Stage {

    private DynamicTestController controller;
    private String path;
    public static SoundManager soundManager;

    public DynamicTestStage(String path, SoundManager soundManager) {
        try {
            DynamicTestStage.soundManager = soundManager;
            this.path = path;
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ru/kit/stabiloapps/fxml/test_ronberga.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.setStage(this);
            this.setTitle("Динамическая проба - стабилометрия");
            this.setMaximized(true);

            this.setScene(new Scene(root));

            this.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    close();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    @Override
    public void close() {

        if (controller.buttonOk.isDisable()) {
            // =========== ALERT DIALOG ==============
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выход");
            alert.setHeaderText("Вы действительно хотите выйти?");
            alert.setContentText("Данные не были сохранены. При выходе данные будут потеряны.");
            // =======================================

            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                cancelTasks();
                super.close();
            }
        } else {
            cancelTasks();
            super.close();
        }
        soundManager.disposeAllSounds();
    }

    private void cancelTasks() {
        List<Task> tasks = controller.getTasks();
        for (Task task : tasks) {
            task.cancel();
        }
        controller.closeAll();
        controller.getStabilo().offDynamicData();
    }
}
