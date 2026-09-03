package com.farheenshaikh.jobfit.parsing.skill;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Default {@link SkillExtractor}: whole-word, case-insensitive matching of resume text
 * against a fixed vocabulary of common tech skills. Deliberately simple and dependency-free
 * so the pipeline works with zero external services; swapping in an LLM-backed extractor
 * behind the same interface is a drop-in upgrade later, not a rewrite.
 */
@Component
public class KeywordSkillExtractor implements SkillExtractor {

    // Canonical display form for each skill; matching is case-insensitive against this list.
    private static final List<String> VOCABULARY = List.of(
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust", "Kotlin",
            "Swift", "Ruby", "PHP", "Scala", "SQL",
            "Spring Boot", "Spring", "React", "Angular", "Vue", "Node.js", "Express",
            "Django", "Flask", "Hibernate", "JPA", "GraphQL", "REST",
            "PostgreSQL", "MySQL", "MongoDB", "Redis", "Cassandra", "Elasticsearch",
            "Docker", "Kubernetes", "Terraform", "Jenkins", "GitHub Actions", "CI/CD",
            "AWS", "Azure", "GCP", "Google Cloud",
            "Git", "Linux", "Microservices", "Kafka", "RabbitMQ", "gRPC", "Protocol Buffers",
            "JUnit", "Maven", "Gradle", "TDD", "Agile", "Scrum",
            "Machine Learning", "TensorFlow", "PyTorch", "Pandas", "NumPy", "scikit-learn",
            "HTML", "CSS", "Tailwind CSS", "Bootstrap"
    );

    @Override
    public List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        Set<String> found = new LinkedHashSet<>();
        for (String skill : VOCABULARY) {
            if (containsWholeWord(text, skill)) {
                found.add(skill);
            }
        }
        return List.copyOf(found);
    }

    private boolean containsWholeWord(String text, String phrase) {
        // Skills like "C++", "CI/CD", "Node.js" have punctuation, so \b word-boundaries
        // don't apply cleanly -- match on whitespace/start-of-string/end-of-string boundaries
        // instead of \b for those, and \b for plain alphanumeric skills.
        String quoted = Pattern.quote(phrase);
        String pattern = phrase.matches("[A-Za-z0-9 ]+")
                ? "(?i)\\b" + quoted + "\\b"
                : "(?i)(?<![A-Za-z0-9])" + quoted + "(?![A-Za-z0-9])";
        return Pattern.compile(pattern).matcher(text).find();
    }
}
