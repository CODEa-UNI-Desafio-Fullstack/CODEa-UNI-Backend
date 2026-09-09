package codea.uni.desafio_fullstack.machinery.interfaces.rest.resources;

public record CreateMachineryResource(String code, Integer MachineryTypeId) {
    public CreateMachineryResource {
        if(code == null){
            throw new NullPointerException("code is null");
        }
        if(MachineryTypeId == null){
            throw new NullPointerException("MachineryTypeId is null");
        }
    }

}
