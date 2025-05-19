package Services;

import Model.*;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.*;

// ... ваш класс ...

public class PlacementOptimizer { // Пример сервисного класса

    static {
        Loader.loadNativeLibraries();
    }

    private double getPreferenceScore(String typeCategory, String cellCategory) {
        // Реализуйте вашу логику приоритетов здесь
        switch (typeCategory) {
            case "A" -> {
                if ("Премиум".equals(cellCategory)) return 1000;
                if ("Стандартный".equals(cellCategory)) return 500;
                if ("Удаленный".equals(cellCategory)) return 50;
            }
            case "B" -> {
                if ("Премиум".equals(cellCategory)) return 400;
                if ("Стандартный".equals(cellCategory)) return 800;
                if ("Удаленный".equals(cellCategory)) return 100;
            }
            case "C" -> {
                if ("Премиум".equals(cellCategory)) return 10;
                if ("Стандартный".equals(cellCategory)) return 40;
                if ("Удаленный".equals(cellCategory)) return 600;
            }
        }
        return 0; // По умолчанию или для непредвиденных комбинаций
    }


    public Map<TypeOfStorageItem, Map<Cell, Integer>> getOptimizePlacement(Map<TypeOfStorageItem,Integer> itemsAndAmount, List<Cell> cells) {

        MPSolver solver = MPSolver.createSolver("SCIP"); // SCIP или CBC_MIXED_INTEGER_PROGRAMMING хорошо подходят
        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return Collections.emptyMap();
        }

        TypeOfStorageItem[] types=itemsAndAmount.keySet().toArray(new TypeOfStorageItem[0]);
        int numItems = types.length;
        int numCells = cells.size();

        // 3. Определение переменных решения y_ij
        MPVariable[][] y = new MPVariable[numItems][numCells];
        for (int i = 0; i < numItems; ++i) {
            for (int j = 0; j < numCells; ++j) {
                // Количество единиц товара i в ячейке j
                // Верхняя граница - либо N_i, либо сколько поместится по весу (C_j / w_i), берем меньшее
                double maxCanFitByWeight = types[i].getWeight() > 0 ?
                        cells.get(j).getPseudoAvailableWeight() / types[i].getWeight() :
                        itemsAndAmount.get(types[i]); // если вес 0, то только по количеству
                double upperBound = Math.min(itemsAndAmount.get(types[i]), maxCanFitByWeight);
                y[i][j] = solver.makeIntVar(0, upperBound, "y_" + i + "_" + j);
            }
        }

        // 4. Определение ограничений
        // Ограничение 1: Вместимость ячеек
        for (int j = 0; j < numCells; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, cells.get(j).getPseudoAvailableWeight(), "Capacity_Cell_" + j);
            for (int i = 0; i < numItems; ++i) {
                constraint.setCoefficient(y[i][j], types[i].getWeight());
            }
        }

        // Ограничение 2: Размещение всех товаров
        for (int i = 0; i < numItems; ++i) {
            MPConstraint constraint = solver.makeConstraint(itemsAndAmount.get(types[i]), itemsAndAmount.get(types[i]),
                    "Demand_Item_" + i);
            for (int j = 0; j < numCells; ++j) {
                constraint.setCoefficient(y[i][j], 1);
            }
        }

        // 5. Определение целевой функции
        MPObjective objective = solver.objective();
        for (int i = 0; i < numItems; ++i) {
            for (int j = 0; j < numCells; ++j) {
                double score = getPreferenceScore(types[i].getGrade(), cells.get(j).getGrade());
                objective.setCoefficient(y[i][j], score);
            }
        }
        objective.setMaximization();

        // 6. Решение модели
        final MPSolver.ResultStatus resultStatus = solver.solve();

        // 7. Обработка результатов
        Map<TypeOfStorageItem, Map<Cell, Integer>> placementResult = new HashMap<>(); // itemID -> {cellID -> quantity}
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.println("Решение найдено!");
            System.out.println("Максимальное значение целевой функции = " + objective.value());
            for (int i = 0; i < numItems; ++i) {
                Map<Cell, Integer> itemPlacements = new HashMap<>();
                for (int j = 0; j < numCells; ++j) {
                    if (y[i][j].solutionValue() > 0.5) { // Для целочисленных > 0 достаточно
                        int quantityPlaced = (int) Math.round(y[i][j].solutionValue());
                        System.out.println("Товар " + types[i].getName() +
                                " (Класс " + types[i].getGrade() +
                                ") в Ячейку ID " + cells.get(j).getID() +
                                " (Категория " + cells.get(j).getGrade() +
                                ") - Количество: " + quantityPlaced);
                        itemPlacements.put(cells.get(j), quantityPlaced);
                    }
                }
                if (!itemPlacements.isEmpty()) {
                    placementResult.put(types[i], itemPlacements);
                }
            }
        } else {
            System.err.println("Оптимальное решение не найдено. Статус: " + resultStatus);
            if (resultStatus == MPSolver.ResultStatus.INFEASIBLE) {
                System.err.println("Задача неразрешима. Проверьте ограничения и входные данные (например, достаточно ли общей вместимости ячеек).");
            } else if (resultStatus == MPSolver.ResultStatus.UNBOUNDED) {
                System.err.println("Задача неограничена. Проверьте целевую функцию и ограничения.");
            }
            return null;
        }
        return placementResult;
    }



}