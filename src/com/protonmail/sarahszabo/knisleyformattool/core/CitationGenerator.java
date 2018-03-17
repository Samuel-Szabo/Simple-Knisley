/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protonmail.sarahszabo.knisleyformattool.core;

/**
 * Generates the Knisley citations for display.
 *
 * @author Sarah Szabo <PhysicistSarah@Gmail.com>
 * @version 1.0
 */
public final class CitationGenerator {

    private final String[] authors;
    private final String title, date, url, publisher, telephoneNumber, pageNumber;
    private final GeneratorType generatorType;

    /**
     * Constructs a new {@link CitationGenerator} with the specified properties.
     *
     * @param authors The authors of the article
     * @param title The title
     * @param date The date of publishing
     * @param url The URL of the article
     * @param publisher The publisher
     * @param telephoneNumber The phone number
     * @param pageNumber The page number
     * @param state The mode for the generator
     */
    public CitationGenerator(String[] authors, String title, String date, String url, String publisher, String telephoneNumber,
            String pageNumber, GeneratorType state) {
        if (authors == null || title == null || date == null || url == null || state == null) {
            throw new NullPointerException("One of the values entered is null");
        } else if (title.isEmpty() || authors[0].isEmpty()) {
            throw new IllegalArgumentException("Author or title is empty");
        }
        this.authors = authors;
        this.title = title;
        this.date = date;
        this.url = url;
        this.publisher = publisher;
        this.telephoneNumber = telephoneNumber;
        this.pageNumber = pageNumber;
        this.generatorType = state;
    }

    /**
     * Generates a citation in the native generator format.
     *
     * @return The citation
     */
    public String generateKnisleyCitation() {
        String citation;
        if (this.authors.length == 1) {
            citation = this.authors[0];
        } else if (this.authors.length == 2) {
            citation = this.authors[0] + " and " + this.authors[1];
        } else {
            citation = this.authors[0] + " et al";
        }
        citation += ". " + this.date + ". " + this.title + ". ";
        if (!this.publisher.isEmpty()) {
            citation += this.publisher + ". ";
        }
        if (!this.telephoneNumber.isEmpty()) {
            citation += this.telephoneNumber + ". ";
        }
        if (!this.pageNumber.isEmpty()) {
            citation += this.pageNumber + ". ";
        }
        if (!this.url.isEmpty()) {
            citation += this.url;
        }
        return citation;
    }

    /**
     * The type of generator.
     */
    public enum GeneratorType {
        /**
         * An article citation generator.
         */
        Article,
        /**
         * A book citation generator.
         */
        Book;
    }
}
