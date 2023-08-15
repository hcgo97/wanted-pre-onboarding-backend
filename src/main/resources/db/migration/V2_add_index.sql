# users
create index index_users_id_is_deleted
    on wanted_internship.users (id, is_deleted);

create index index_users_email_is_deleted
    on wanted_internship.users (email, is_deleted);

# posts
create index index_posts_id_is_deleted
    on wanted_internship.posts (id, is_deleted);

create index index_posts_user_id_is_deleted
    on wanted_internship.posts (user_id, is_deleted);
