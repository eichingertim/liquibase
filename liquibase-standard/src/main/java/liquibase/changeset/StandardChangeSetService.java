package liquibase.changeset;

import liquibase.change.AbstractSQLChange;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.ModifyChangeSets;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.util.ObjectUtil;

/**
 *
 * The standard OSS implementation of the ChangeSetService
 *
 */
public class StandardChangeSetService implements ChangeSetService {
    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    @Override
    public ChangeSet createChangeSet(DatabaseChangeLog changeLog) {
        return new ChangeSet(changeLog);
    }

    @Override
    public ChangeSet createChangeSet(String id, String author, boolean alwaysRun, boolean runOnChange, String filePath, String contextFilter, String dbmsList, DatabaseChangeLog databaseChangeLog) {
        return new ChangeSet(id, author, alwaysRun, runOnChange, filePath, contextFilter, dbmsList, null, null, true, ObjectQuotingStrategy.LEGACY, databaseChangeLog);
    }

    @Override
    public ChangeSet createChangeSet(String id, String author, boolean alwaysRun, boolean runOnChange,
                                     String filePath, String contextFilter, String dbmsList,
                                     String runWith, String runWithSpoolFile, boolean runInTransaction,
                                     ObjectQuotingStrategy quotingStrategy, DatabaseChangeLog databaseChangeLog) {
        return new ChangeSet(id, author, alwaysRun, runOnChange,
                DatabaseChangeLog.normalizePath(filePath),
                contextFilter, dbmsList, runWith, runWithSpoolFile, runInTransaction,
                quotingStrategy, databaseChangeLog);
    }
    @Override
    public ModifyChangeSets createModifyChangeSets(ParsedNode node) throws ParsedNodeException {
        Object stripCommentsValue = node.getChildValue(null, "stripComments");
        boolean stripComments = false;
        if (stripCommentsValue != null) {
            stripComments = ObjectUtil.convert(stripCommentsValue, Boolean.class);
        }
        return new ModifyChangeSets(
                (String) node.getChildValue(null, "runWith"),
                (String) node.getChildValue(null, "runWithSpoolFile"),
                stripComments);
    }

    @Override
    public void modifyChangeSets(ChangeSet changeSet, ModifyChangeSets modifyChangeSets) {
        if (changeSet.getRunWith() == null) {
            changeSet.setRunWith(modifyChangeSets != null ? modifyChangeSets.getRunWith() : null);
        }
        if (changeSet.getRunWithSpoolFile() == null) {
            changeSet.setRunWithSpoolFile(modifyChangeSets != null ? modifyChangeSets.getRunWithSpool() : null);
        }
        for (Change change : changeSet.getChanges()) {
            if (change instanceof AbstractSQLChange &&
                ((AbstractSQLChange)change).isStripCommentsUsedDefaultValue() &&
                modifyChangeSets != null && modifyChangeSets.isStripComments() != null) {
                ((AbstractSQLChange)change).setStripComments(modifyChangeSets.isStripComments());
            }
        }
    }
}
