/*
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

// ... ваш класс ...

public class PlacementOptimizer { // Пример сервисного класса

    static {
        Loader.loadNativeLibraries(); // Загрузка нативных библиотек OR-Tools
    }

    // Предположим, у вас есть вспомогательные классы для удобства
    static class ItemToPlace {
        long id;
        TypeOfStorageItem.ABCCategory abcCategory;
        double weightPerUnit;
        int quantityToPlace;
        // конструктор и геттеры
        public ItemToPlace(long id, TypeOfStorageItem.ABCCategory abcCategory, double weightPerUnit, int quantityToPlace) {
            this.id = id;
            this.abcCategory = abcCategory;
            this.weightPerUnit = weightPerUnit;
            this.quantityToPlace = quantityToPlace;
        }
        // геттеры
        public long getId() { return id; }
        public TypeOfStorageItem.ABCCategory getAbcCategory() { return abcCategory; }
        public double getWeightPerUnit() { return weightPerUnit; }
        public int getQuantityToPlace() { return quantityToPlace; }
    }

    // Вам нужно будет добавить поле категории в вашу модель Cell
    // public enum CellRank { PREMIUM, STANDARD, BASIC }
    // private CellRank cellRank;
    static class StorageCell {
        long id;
        // Cell.CellRank cellRank; // Ваша категория ячейки (JA, JB, JC)
        String cellCategory; // Пока используем String для примера (JA, JB, JC)
        double availableCapacity; // C_j
        // конструктор и геттеры
        public StorageCell(long id, String cellCategory, double availableCapacity) {
            this.id = id;
            this.cellCategory = cellCategory;
            this.availableCapacity = availableCapacity;
        }
        // геттеры
        public long getId() { return id; }
        public String getCellCategory() { return cellCategory; }
        public double getAvailableCapacity() { return availableCapacity; }

    }

    private double getPreferenceScore(TypeOfStorageItem.ABCCategory itemClass, String cellCategory) {
        // Реализуйте вашу логику приоритетов здесь
        if (itemClass == TypeOfStorageItem.ABCCategory.A) {
            if ("JA".equals(cellCategory)) return 1000;
            if ("JB".equals(cellCategory)) return 500;
            if ("JC".equals(cellCategory)) return 50;
        } else if (itemClass == TypeOfStorageItem.ABCCategory.B) {
            if ("JA".equals(cellCategory)) return 400;
            if ("JB".equals(cellCategory)) return 800;
            if ("JC".equals(cellCategory)) return 100;
        } else if (itemClass == TypeOfStorageItem.ABCCategory.C) {
            if ("JA".equals(cellCategory)) return 10;
            if ("JB".equals(cellCategory)) return 40;
            if ("JC".equals(cellCategory)) return 600;
        }
        return 0; // По умолчанию или для непредвиденных комбинаций
    }


    public Map<Long, Map<Long, Integer>> optimizePlacement(List<ItemToPlace> items, List<StorageCell> cells) {
        MPSolver solver = MPSolver.createSolver("SCIP"); // SCIP или CBC_MIXED_INTEGER_PROGRAMMING хорошо подходят
        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return Collections.emptyMap();
        }

        int numItems = items.size();
        int numCells = cells.size();

        // 3. Определение переменных решения y_ij
        MPVariable[][] y = new MPVariable[numItems][numCells];
        for (int i = 0; i < numItems; ++i) {
            for (int j = 0; j < numCells; ++j) {
                // Количество единиц товара i в ячейке j
                // Верхняя граница - либо N_i, либо сколько поместится по весу (C_j / w_i), берем меньшее
                double maxCanFitByWeight = items.get(i).getWeightPerUnit() > 0 ?
                        cells.get(j).getAvailableCapacity() / items.get(i).getWeightPerUnit() :
                        items.get(i).getQuantityToPlace(); // если вес 0, то только по количеству
                double upperBound = Math.min(items.get(i).getQuantityToPlace(), maxCanFitByWeight);
                y[i][j] = solver.makeIntVar(0, upperBound, "y_" + i + "_" + j);
            }
        }

        // 4. Определение ограничений
        // Ограничение 1: Вместимость ячеек
        for (int j = 0; j < numCells; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, cells.get(j).getAvailableCapacity(), "Capacity_Cell_" + j);
            for (int i = 0; i < numItems; ++i) {
                constraint.setCoefficient(y[i][j], items.get(i).getWeightPerUnit());
            }
        }

        // Ограничение 2: Размещение всех товаров
        for (int i = 0; i < numItems; ++i) {
            MPConstraint constraint = solver.makeConstraint(items.get(i).getQuantityToPlace(), items.get(i).getQuantityToPlace(), "Demand_Item_" + i);
            for (int j = 0; j < numCells; ++j) {
                constraint.setCoefficient(y[i][j], 1);
            }
        }

        // 5. Определение целевой функции
        MPObjective objective = solver.objective();
        for (int i = 0; i < numItems; ++i) {
            for (int j = 0; j < numCells; ++j) {
                double score = getPreferenceScore(items.get(i).getAbcCategory(), cells.get(j).getCellCategory());
                objective.setCoefficient(y[i][j], score);
            }
        }
        objective.setMaximization();

        // 6. Решение модели
        final MPSolver.ResultStatus resultStatus = solver.solve();

        // 7. Обработка результатов
        Map<Long, Map<Long, Integer>> placementResult = new HashMap<>(); // itemID -> {cellID -> quantity}
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.println("Решение найдено!");
            System.out.println("Максимальное значение целевой функции = " + objective.value());
            for (int i = 0; i < numItems; ++i) {
                Map<Long, Integer> itemPlacements = new HashMap<>();
                for (int j = 0; j < numCells; ++j) {
                    if (y[i][j].solutionValue() > 0.5) { // Для целочисленных > 0 достаточно
                        int quantityPlaced = (int) Math.round(y[i][j].solutionValue());
                        System.out.println("Товар ID " + items.get(i).getId() +
                                " (Класс " + items.get(i).getAbcCategory() +
                                ") в Ячейку ID " + cells.get(j).getId() +
                                " (Категория " + cells.get(j).getCellCategory() +
                                ") - Количество: " + quantityPlaced);
                        itemPlacements.put(cells.get(j).getId(), quantityPlaced);
                    }
                }
                if (!itemPlacements.isEmpty()) {
                    placementResult.put(items.get(i).getId(), itemPlacements);
                }
            }
        } else {
            System.err.println("Оптимальное решение не найдено. Статус: " + resultStatus);
            if (resultStatus == MPSolver.ResultStatus.INFEASIBLE) {
                System.err.println("Задача неразрешима. Проверьте ограничения и входные данные (например, достаточно ли общей вместимости ячеек).");
            } else if (resultStatus == MPSolver.ResultStatus.UNBOUNDED) {
                System.err.println("Задача неограничена. Проверьте целевую функцию и ограничения.");
            }
        }
        return placementResult;
    }
}*/
