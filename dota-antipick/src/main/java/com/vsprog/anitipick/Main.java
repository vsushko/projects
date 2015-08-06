package com.vsprog.anitipick;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DOTA ANTI PICK Application
 */
public class Main extends Application {
    public static final int ROW_COUNT = 7;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 930, 930);
        stage.setScene(scene);
        stage.setTitle("Dota antipick");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(2);
        grid.setHgap(2);

        scene.setRoot(grid);

        HeroesBuilder heroesBuilder = new HeroesBuilder();
        List<Hero> heroes = heroesBuilder.loadHeroesInfo();

        List<String> heroNames = new ArrayList<String>();

        for (Hero hero : heroes) {
            heroNames.add(hero.getName());
        }

        List<Image> images = heroesBuilder.loadImageHeroes(heroNames);
        System.out.println(images.size());

        int heroesCount = heroes.size();
        int imageColumn = 0;
        int imageRow = 0;

        for (int i = 0; i < heroesCount; i++) {
            ImageView imageView = new ImageView(images.get(i));

            final int count = i;
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Dialog message");
                    alert.setContentText("This is " + heroes.get(count).getName());
                    alert.showAndWait();
                    event.consume();
                }
            });

            grid.add(imageView, imageColumn, imageRow);
            imageColumn++;

            if (imageColumn > ROW_COUNT) {
                imageColumn = 0;
                imageRow++;
            }
        }

        stage.show();

        // пик противника
        Pick pick = new Pick();
        pick.setFirstHero(heroesBuilder.getHeroByName(heroes, "Enigma"));
        pick.setSecondHero(heroesBuilder.getHeroByName(heroes, "Phantom Lancer"));
        pick.setThirdHero(heroesBuilder.getHeroByName(heroes, "Medusa"));
        pick.setFourthHero(heroesBuilder.getHeroByName(heroes, "Axe"));
        pick.setFifthHero(heroesBuilder.getHeroByName(heroes, "Silencer"));


        // герои антипики
        List<String> firstHeroEnemies = new ArrayList<String>();
        firstHeroEnemies.addAll(pick.getFirstHero().getEnemies());

        List<String> secondHeroEnemies = new ArrayList<String>();
        secondHeroEnemies.addAll(pick.getSecondHero().getEnemies());

        List<String> thirdHeroEnemies = new ArrayList<String>();
        thirdHeroEnemies.addAll(pick.getThirdHero().getEnemies());

        List<String> fourthHeroEnemies = new ArrayList<String>();
        fourthHeroEnemies.addAll(pick.getFourthHero().getEnemies());

        List<String> fifthHeroEnemies = new ArrayList<String>();
        fifthHeroEnemies.addAll(pick.getFifthHero().getEnemies());

        System.out.println(firstHeroEnemies.size() + secondHeroEnemies.size() + thirdHeroEnemies.size() + fourthHeroEnemies.size() + fifthHeroEnemies.size());

        List<String> enemies = new ArrayList<String>();
        enemies.addAll(firstHeroEnemies);
        enemies.addAll(secondHeroEnemies);
        enemies.addAll(thirdHeroEnemies);
        enemies.addAll(fourthHeroEnemies);
        enemies.addAll(fifthHeroEnemies);

        List<HeroWeight> weights = new ArrayList<HeroWeight>();

        // посчитаем веса для каждого героя из первого списка антипика в остальных
        // рассчитываем вероятность некоторых героев
        for (String heroName : firstHeroEnemies) {
            int w = 0;

            w += findHeroCountInAntipickList(heroName, secondHeroEnemies);
            w += findHeroCountInAntipickList(heroName, thirdHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fourthHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fifthHeroEnemies);
            System.out.println("Герой: " + heroName + " имеет следующий вес " + w + "  в первом списке антипика");
            weights.add(new HeroWeight(heroName, w));
        }

        for (String heroName : secondHeroEnemies) {
            int w = 0;

            w += findHeroCountInAntipickList(heroName, firstHeroEnemies);
            w += findHeroCountInAntipickList(heroName, thirdHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fourthHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fifthHeroEnemies);
            System.out.println("Герой: " + heroName + " имеет следующий вес " + w + "  во втором списке антипика");
            addHeroWeights(new HeroWeight(heroName, w), weights);
        }

        for (String heroName : thirdHeroEnemies) {
            int w = 0;

            w += findHeroCountInAntipickList(heroName, firstHeroEnemies);
            w += findHeroCountInAntipickList(heroName, secondHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fourthHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fifthHeroEnemies);
            System.out.println("Герой: " + heroName + " имеет следующий вес " + w + "  в третьем списке антипика");
            addHeroWeights(new HeroWeight(heroName, w), weights);
        }

        for (String heroName : fourthHeroEnemies) {
            int w = 0;

            w += findHeroCountInAntipickList(heroName, firstHeroEnemies);
            w += findHeroCountInAntipickList(heroName, secondHeroEnemies);
            w += findHeroCountInAntipickList(heroName, thirdHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fifthHeroEnemies);
            System.out.println("Герой: " + heroName + " имеет следующий вес " + w + "  в четвертом списке антипика");
            addHeroWeights(new HeroWeight(heroName, w), weights);
        }

        for (String heroName : fifthHeroEnemies) {
            int w = 0;

            w += findHeroCountInAntipickList(heroName, firstHeroEnemies);
            w += findHeroCountInAntipickList(heroName, secondHeroEnemies);
            w += findHeroCountInAntipickList(heroName, thirdHeroEnemies);
            w += findHeroCountInAntipickList(heroName, fourthHeroEnemies);
            System.out.println("Герой: " + heroName + " имеет следующий вес " + w + "  в пятом списке антипика");
            addHeroWeights(new HeroWeight(heroName, w), weights);
        }

        System.out.println(firstHeroEnemies.size() + secondHeroEnemies.size() + thirdHeroEnemies.size() + fourthHeroEnemies.size() + fifthHeroEnemies.size());

    }

    private void addHeroWeights(HeroWeight heroWeight, List<HeroWeight> weights) {
        for (HeroWeight weight : weights) {
            if (weight.getHeroName().equals(heroWeight.getHeroName())) {
                weight.setHeroWeight(weight.getHeroWeight() + heroWeight.getHeroWeight());
            }
        }
    }

    private int findHeroCountInAntipickList(String heroName, List<String> heroEnemies) {
        int w = 0;
        for (String enemyName : heroEnemies) {
            if (enemyName.equals(heroName)) {
                w++;
            }
        }
        return w;
    }
}