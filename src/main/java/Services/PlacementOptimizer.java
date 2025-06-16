package Services;

import Model.*;

import java.util.*;
import java.util.stream.Collectors;


public class PlacementOptimizer {


    private double getPreferenceScore(String typeCategory, String cellCategory) {
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
        return 0;
    }

    private static class PotentialPlacement {
        TypeOfStorageItem item;
        Cell cell;
        double score;

        PotentialPlacement(TypeOfStorageItem item, Cell cell, double score) {
            this.item = item;
            this.cell = cell;
            this.score = score;
        }
    }

    public Map<TypeOfStorageItem, Map<Cell, Integer>> getOptimizePlacement(Map<TypeOfStorageItem,Integer> itemsAndAmount, List<Cell> cells) {

        if (itemsAndAmount == null || itemsAndAmount.isEmpty() || itemsAndAmount.values().stream().allMatch(amount -> amount <= 0)) {
            System.out.println("Нет товаров для размещения.");
            return Collections.emptyMap();
        }

        Map<TypeOfStorageItem, Integer>
                actualItemsToPlace = itemsAndAmount.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (actualItemsToPlace.isEmpty()) {
            System.out.println("Нет товаров для размещения (все количества <= 0).");
            return Collections.emptyMap();
        }


        if (cells == null || cells.isEmpty()) {
            System.err.println("Нет доступных ячеек для размещения товаров.");
            return null;
        }

        Map<TypeOfStorageItem, Map<Cell, Integer>> placementResult = new HashMap<>();
        Map<TypeOfStorageItem, Integer> remainingItemsToPlace = new HashMap<>(actualItemsToPlace);
        Map<Cell, Double> remainingCellCapacities = new HashMap<>();
        for (Cell cell : cells) {
            remainingCellCapacities.put(cell, cell.getPseudoAvailableWeight());
        }

        List<PotentialPlacement> potentialPlacements = new ArrayList<>();
        for (TypeOfStorageItem item : remainingItemsToPlace.keySet()) {
            for (Cell cell : cells) {
                double score = getPreferenceScore(item.getGrade(), cell.getGrade());
                potentialPlacements.add(new PotentialPlacement(item, cell, score));
            }
        }

        potentialPlacements.sort((p1, p2) -> Double.compare(p2.score, p1.score));

        for (PotentialPlacement pp : potentialPlacements) {
            TypeOfStorageItem item = pp.item;
            Cell cell = pp.cell;

            int neededQuantityOfItem = remainingItemsToPlace.getOrDefault(item, 0);
            if (neededQuantityOfItem == 0) {
                continue;
            }

            double currentCellCapacityWeight = remainingCellCapacities.get(cell);
            double itemWeight = item.getWeight();

            if (currentCellCapacityWeight <= 0 && itemWeight > 0.00001) {
                continue;
            }

            int maxUnitsCanFitInCell;
            if (itemWeight > 0.00001) {
                if (currentCellCapacityWeight < itemWeight) {
                    maxUnitsCanFitInCell = 0;
                } else {
                    maxUnitsCanFitInCell = (int) Math.floor(currentCellCapacityWeight / itemWeight);
                }
            } else {
                maxUnitsCanFitInCell = neededQuantityOfItem;
            }

            int quantityToPlace = Math.min(neededQuantityOfItem, maxUnitsCanFitInCell);
            quantityToPlace = Math.max(0, quantityToPlace);

            if (quantityToPlace > 0) {
                placementResult.computeIfAbsent(item, k -> new HashMap<>())
                        .merge(cell, quantityToPlace, Integer::sum);

                remainingItemsToPlace.put(item, neededQuantityOfItem - quantityToPlace);

                if (itemWeight > 0.00001) {
                    remainingCellCapacities.put(cell, currentCellCapacityWeight - (quantityToPlace * itemWeight));
                }
            }
        }

        long totalRemainingCount = 0;
        for (Map.Entry<TypeOfStorageItem, Integer> entry : remainingItemsToPlace.entrySet()) {
            if (entry.getValue() > 0) {
                System.err.println("Не удалось разместить все товары. Товар " + entry.getKey().getName() +
                        " (Класс " + entry.getKey().getGrade() + ")" +
                        " осталось " + entry.getValue() + " единиц.");
                totalRemainingCount += entry.getValue();
            }
        }

        if (totalRemainingCount > 0) {
            System.err.println("Жадное размещение не смогло разместить все товары. Общее количество неразмещенных единиц: " + totalRemainingCount);
            return null;
        }

        if (placementResult.isEmpty() && !actualItemsToPlace.isEmpty()) {
            boolean anyItemHadPositiveDemand = actualItemsToPlace.values().stream().anyMatch(v -> v > 0);
            if (anyItemHadPositiveDemand) {
                System.err.println("Ни один товар не был размещен, хотя были товары для размещения. Проверьте вместимость ячеек и вес товаров.");
                return null;
            }
        }


        System.out.println("Жадное размещение завершено.");
        double totalScoreCalculated = 0;
        if (!placementResult.isEmpty()) {
            System.out.println("Результаты размещения:");
            for (Map.Entry<TypeOfStorageItem, Map<Cell, Integer>> entry : placementResult.entrySet()) {
                TypeOfStorageItem item = entry.getKey();
                for (Map.Entry<Cell, Integer> cellEntry : entry.getValue().entrySet()) {
                    Cell cell = cellEntry.getKey();
                    int quantity = cellEntry.getValue();
                    double scoreForItemInCell = getPreferenceScore(item.getGrade(), cell.getGrade());
                    totalScoreCalculated += quantity * scoreForItemInCell;
                    System.out.println("  Товар " + item.getName() +
                            " (Класс " + item.getGrade() +
                            ") в Ячейку ID " + cell.getID() +
                            " (Категория " + cell.getGrade() +
                            ") - Количество: " + quantity +
                            " (Счет за ед.: " + scoreForItemInCell +")");
                }
            }
            System.out.println("Общий расчетный счет (жадный алгоритм): " + totalScoreCalculated);
        } else if (actualItemsToPlace.isEmpty()) {
            System.out.println("Нет товаров для размещения, результат пуст.");
        } else {
            System.out.println("Размещение завершено, но результат пуст (возможно, все товары имели количество 0).");
        }


        return placementResult;
    }



}