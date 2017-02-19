package org.innovateuk.ifs.category.domain;

import javax.persistence.*;

/**
 * NOTICE: THIS CLASS SHOULD NOT BE EXTENDED OR USED DIRECTLY IN ANY MEANINGFUL WAY.
 *
 * It only serves to circumvent JPA cascading issues & foreign key
 * constraints. This is primarily around deletion of {@link Category}
 * cascading down to related {@link CategoryLink}s.
 *
 * Reasoning:
 *
 * Due to {@link CategoryLink} being an abstract superclass it is
 * impossible to retrieve {@link Category#links} if it is typed
 * against {@link CategoryLink} (as we cannot create
 * a collection of unknown {@link CategoryLink} subtypes).
 *
 * Consequently, there is no concrete collection for JPA to
 * cascade deletes against. Attempting to delete the {@link Category}
 * will then throw foreign key constraint errors due to trying to
 * remove the `category` row before all `category_links` have been
 * cleared out.
 *
 * As a way around this, this class serves as a concrete class that
 * the maps against `category_link` rows in a <strong>NON-POLYMORPHIC</strong>
 * manner (it is no longer concerned about row type discrimination).
 *
 * Yes, this is dirty and feels wrong.
 * Yes, this could lead to weird scenarios.
 *
 * However, it does work.
 *
 * @param <C> the type of Category linked.
 */
@Entity
@Table(name = "category_link")
class CategoryInnerLink<C extends Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Category.class)
    @JoinColumn(name="categoryId", referencedColumnName="id")
    private C category;
}
