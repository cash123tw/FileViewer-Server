package App.Init;

import Web.Service.LocalFileWalker.Serv_localWalk_impl;
import lombok.NoArgsConstructor;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;

@NoArgsConstructor
@Order(2)
/**
 * Use to Init Hibernate Param
 */
public class InitHinernateParam implements ApplicationListener<ContextRefreshedEvent>, MetadataBuilderContributor {

    private StartMode startMode;

    public InitHinernateParam(StartMode startMode) {
        this.startMode = startMode;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (startMode.equals(StartMode.NEW) || startMode.equals(StartMode.RESCAN)) {
            Serv_localWalk_impl bean
                    = event
                    .getApplicationContext()
                    .getBean(Serv_localWalk_impl.class);
            bean.walkLocalFileAndSaveToRepository();

            if (startMode.equals(StartMode.RESCAN)) {
                bean.checkAllFilePathExists();
            }
        }
    }

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder
                .applySqlFunction(
                        "group_concat",
                        new StandardSQLFunction("group_concat", StandardBasicTypes.STRING)
                );
    }

    public enum StartMode {
        RESCAN, NEW, NON
    }
}
