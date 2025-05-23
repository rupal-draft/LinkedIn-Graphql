package com.linkedIn.LinkedIn.App.post.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.post.entity.enums.Category;
import com.linkedIn.LinkedIn.App.post.entity.enums.Visibility;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts",indexes = {
        @Index(columnList = "user_id", name = "post_user_id_idx"),
        @Index(columnList = "category", name = "post_category_idx")
})
@NamedEntityGraph(
        name = "Post.withLikesAndCommentsAndUser",
        attributeNodes = {
                @NamedAttributeNode("likes"),
                @NamedAttributeNode("comments"),
                @NamedAttributeNode(value = "user", subgraph = "userDetails")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "userDetails",
                        attributeNodes = {
                                @NamedAttributeNode("name"),
                                @NamedAttributeNode("role"),
                                @NamedAttributeNode("profilePicture")
                        }
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, length = 3000)
    private String content;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Like> likes = new HashSet<>();;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();;

    @Column(name = "deleted")
    private boolean deleted = false;
}
