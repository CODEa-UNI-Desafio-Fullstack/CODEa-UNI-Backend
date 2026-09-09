package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record DeleteMachineryCommand(String code) {
    public DeleteMachineryCommand{
        if(code == null){
            throw new IllegalArgumentException("code cannot be null");
        }
    }
}
