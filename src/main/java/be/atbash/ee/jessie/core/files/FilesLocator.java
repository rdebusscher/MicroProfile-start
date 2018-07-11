/*
 * Copyright 2017-2018 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.jessie.core.files;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 */
@ApplicationScoped
public class FilesLocator {

    private List<FileIdentification> fileIdentifications;
    private List<String> fileNames;

    @PostConstruct
    public void init() {
        defineResources(Pattern.compile(".*\\.tpl"));
    }

    public String findFile(String name, Set<String> alternatives) {
        List<FileIdentification> candidates = fileIdentifications
                .stream()
                .filter(fi -> fi.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        int result = -1; // not found
        if (!candidates.isEmpty()) {
            if (candidates.size() == 1) {
                result = fileIdentifications.indexOf(candidates.get(0));
            } else {
                int candidateMatch = selectBasedOnAlternatives(candidates, alternatives);
                if (candidateMatch > -1) {
                    result = fileIdentifications.indexOf(candidates.get(candidateMatch));
                }
            }
        }

        return String.valueOf(result);
    }

    private int selectBasedOnAlternatives(List<FileIdentification> candidates, Set<String> alternatives) {
        int result = -1;
        boolean multipleMatch = false;

        int idx = alternatives.size();
        List<String> alternativesList = new ArrayList<>(alternatives);
        while (idx > 0 && result == -1 && !multipleMatch) {
            List<Set<String>> alternativeSets = createAlternativeSets(alternativesList, idx);

            List<FileIdentification> matches = new ArrayList<>();
            for (Set<String> alternativeSet : alternativeSets) {

                matches.addAll(candidates.stream()
                        .filter(fi -> fi.getAlternatives().containsAll(alternativeSet)
                                //exact match needed, so check in both ways.
                                && alternativeSet.containsAll(fi.getAlternatives()))
                        .collect(Collectors.toList()));
            }

            if (matches.isEmpty()) {
                idx--; // We don't have a candidate which matches one of the alternative sets.
            } else {
                if (matches.size() == 1) {
                    result = candidates.indexOf(matches.get(0)); // We have a single match, this is the one.
                } else {
                    multipleMatch = true; // We have multiple matches, so we can't decide which is the choosen one.
                }
            }
        }

        if (result == -1 && !multipleMatch) {
            // Didn't found anything which matches exact, but we didn't stop looking because there was a multiple match.
            // Are there versions without any alternative.
            List<FileIdentification> matches = new ArrayList<>();
            matches.addAll(candidates.stream()
                    .filter(fi -> fi.getAlternatives().isEmpty())
                    .collect(Collectors.toList()));

            if (matches.size() == 1) {
                result = candidates.indexOf(matches.get(0)); // We have a single match, this is the one.
            }
            // In all other cases result stays on -1 because we didn't found any suitable match
        }
        return result;
    }

    private List<Set<String>> createAlternativeSets(List<String> alternatives, int numberOfItems) {

        int totalItems = alternatives.size();
        byte[][] combinations = new byte[(int) Math.pow(totalItems, numberOfItems)][numberOfItems];

        // do each column separately
        for (byte j = 0; j < numberOfItems; j++) {
            // for this column, repeat each option in the set 'reps' times
            int reps = (int) Math.pow(totalItems, j);

            // for each column, repeat the whole set of options until we reach the end
            int counter = 0;
            while (counter < combinations.length) {
                // for each option
                for (byte i = 0; i < totalItems; i++) {
                    // save each option 'reps' times
                    for (int k = 0; k < reps; k++) {
                        combinations[counter + i * reps + k][j] = i;
                    }
                }
                // increase counter by 'reps' times amount of actions
                counter += reps * totalItems;
            }
        }

        List<Set<String>> uniqueCombinations = new ArrayList<>();
        for (byte[] setOfActions : combinations) {
            Set<String> item = new HashSet<>();
            for (byte b : setOfActions) {
                item.add(alternatives.get(b));
            }
            if (item.size() == numberOfItems && !uniqueCombinations.contains(item)) {
                uniqueCombinations.add(item);
            }
        }
        return uniqueCombinations;
    }

    public String getTemplateFile(String index) {
        return fileNames.get(Integer.valueOf(index));
    }

    private void defineResources(Pattern pattern) {
        Reflections reflections = new Reflections("files", new ResourcesScanner());
        Set<String> resources = reflections.getResources(Pattern.compile(".*\\.tpl"));



        /*

        String classPath = System.getProperty("java.class.path", ".");

        String[] classPathElements = classPath.split(System.getProperty("path.separator"));

        List<String> resources = new ArrayList<>();
        try {
            for (String element : classPathElements) {
                resources.addAll(getResources(element, pattern));
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
        */

        // TODO Create a system where we can 'override' the files from external.
        fileIdentifications = new ArrayList<>();
        fileNames = new ArrayList<>();
        for (String resource : resources) {
            // Strip .tpl
            String fileName = resource.substring(0, resource.length() - 4);

            fileIdentifications.add(new FileIdentification(fileName, "files"));
            fileNames.add(resource);
        }

    }

    private List<String> getResources(String element, Pattern pattern) throws IOException {
        List<String> result = new ArrayList<>();
        File file = new File(element);
        if (file.isDirectory()) {
            result.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            result.addAll(getResourcesFromJarFile(file, pattern));
        }
        return result;
    }

    private Collection<String> getResourcesFromJarFile(File file, Pattern pattern) throws IOException {
        List<String> result = new ArrayList<>();
        ZipFile zf;
        zf = new ZipFile(file);

        Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            String fileName = ze.getName();
            boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
                result.add(fileName);
            }
        }
        zf.close();
        return result;
    }

    private List<String> getResourcesFromDirectory(File directory, Pattern pattern) throws IOException {
        List<String> result = new ArrayList<>();
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    result.addAll(getResourcesFromDirectory(file, pattern));
                } else {
                    String fileName = file.getCanonicalPath();
                    boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        result.add(fileName);
                    }
                }
            }
        }
        return result;
    }

    private static class FileIdentification {
        private static final Pattern FILE_PATH_PATTERN_SPLIT = Pattern.compile("\\\\|/");
        private Set<String> alternatives;
        private String name;

        public FileIdentification(String fileName, String root) {
            alternatives = new HashSet<>();

            String[] fileParts = FILE_PATH_PATTERN_SPLIT.split(fileName);
            boolean markerFound = false;
            for (int i = 0; i < fileParts.length - 1; i++) {
                // -1 Because the last one is the name of the file
                if (markerFound) {
                    alternatives.add(fileParts[i]);
                } else {
                    markerFound = fileParts[i].equalsIgnoreCase(root);
                }
            }
            this.name = fileParts[fileParts.length - 1];

        }

        public Set<String> getAlternatives() {
            return alternatives;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FileIdentification)) {
                return false;
            }

            FileIdentification that = (FileIdentification) o;

            if (!alternatives.equals(that.alternatives)) {
                return false;
            }
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = alternatives.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
