package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.UpdateMachineryHourMeterCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.UpdateMachineryHourMeterResource;

public class UpdateMachineryHourMeterCommandFromResourceAssembler {
    public static UpdateMachineryHourMeterCommand toCommandFromResource(UpdateMachineryHourMeterResource resource) {
        return new UpdateMachineryHourMeterCommand(resource.code(), resource.hours());
    }
}
