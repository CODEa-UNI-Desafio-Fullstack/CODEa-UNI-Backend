package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record DeleteMachineryTypeCommmand(Integer typeId) {
    public DeleteMachineryTypeCommmand {
        if (typeId == null) {
            throw new IllegalArgumentException("Type id cannot be null");
        }
    }
}
