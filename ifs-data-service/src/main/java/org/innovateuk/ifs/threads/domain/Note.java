package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("NOTE")
public class Note extends Thread {}
